package engine.sound;

import org.lwjgl.openal.*;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.ALC10.*;
import static org.lwjgl.stb.STBVorbis.*;
import static org.lwjgl.system.MemoryStack.stackPush;

/**
 * LWJGL-based audio manager for HodgePodgeBarrage
 * Supports music with intro/loop sections and sound effects
 * Preloads all OGG files for instant playback
 */
public class LwjglAudioManager {

    private long device;
    private long context;

    // Audio sources for different types
    private final Map<String, Integer> musicSources = new ConcurrentHashMap<>();
    private final Map<String, Integer> sfxSources = new ConcurrentHashMap<>();
    private final Map<String, AudioBuffer> loadedBuffers = new ConcurrentHashMap<>();

    // Music state management
    private String currentMusic = null;
    private String currentMusicIntro = null;
    private String currentMusicLoop = null;
    private boolean introFinished = false;

    // Volume controls
    private float masterVolume = 1.0f;
    private float musicVolume = 0.8f;
    private float sfxVolume = 1.0f;

    public LwjglAudioManager() {
        initializeOpenAL();
        preloadAllAudioFiles();
    }

    private void initializeOpenAL() {
        // Initialize OpenAL
        device = alcOpenDevice((ByteBuffer) null);
        if (device == 0) {
            throw new RuntimeException("Failed to open OpenAL device");
        }

        context = alcCreateContext(device, (IntBuffer) null);
        if (context == 0) {
            alcCloseDevice(device);
            throw new RuntimeException("Failed to create OpenAL context");
        }

        alcMakeContextCurrent(context);
        AL.createCapabilities(ALC.createCapabilities(device));

        // Set up listener (player position)
        alListener3f(AL_POSITION, 0, 0, 0);
        alListener3f(AL_VELOCITY, 0, 0, 0);

        System.out.println("LWJGL Audio Manager initialized");
    }

    /**
     * Preload all OGG files from the sounds folder
     */
    private void preloadAllAudioFiles() {
        System.out.println("Starting audio preload...");
        
        try {
            // Get the sounds folder from resources
            URI soundsUri = getClass().getResource("/sounds").toURI();
            
            Path soundsPath;
            if (soundsUri.getScheme().equals("jar")) {
                // Running from JAR - need to use FileSystem
                FileSystem fileSystem = FileSystems.newFileSystem(soundsUri, Collections.emptyMap());
                soundsPath = fileSystem.getPath("/sounds");
            } else {
                // Running from IDE - direct file access
                soundsPath = Paths.get(soundsUri);
            }
            
            // Find all OGG files
            try (Stream<Path> files = Files.walk(soundsPath)) {
                files.filter(Files::isRegularFile)
                     .filter(path -> path.toString().toLowerCase().endsWith(".ogg"))
                     .forEach(this::preloadAudioFile);
            }
            
            System.out.println("Audio preload completed! Loaded " + loadedBuffers.size() + " files:");
            for (String filename : loadedBuffers.keySet()) {
                System.out.println("  - " + filename);
            }
            
        } catch (URISyntaxException | IOException e) {
            System.err.println("Failed to preload audio files: " + e.getMessage());
            System.out.println("Audio files will be loaded on-demand instead.");
        }
    }

    /**
     * Preload a single audio file
     */
    private void preloadAudioFile(Path filePath) {
        try {
            String filename = filePath.getFileName().toString();
            
            if (loadedBuffers.containsKey(filename)) {
                return; // Already loaded
            }
            
            System.out.println("Preloading: " + filename);
            
            AudioBuffer buffer = loadOGGFromPath(filePath);
            if (buffer != null) {
                loadedBuffers.put(filename, buffer);
            }
            
        } catch (Exception e) {
            System.err.println("Failed to preload: " + filePath.getFileName() + " - " + e.getMessage());
        }
    }

    /**
     * Load OGG file from a specific path (for preloading)
     */
    private AudioBuffer loadOGGFromPath(Path filePath) {
        try (MemoryStack stack = stackPush()) {
            // Load OGG file from path
            ByteBuffer audioData = loadFileFromPath(filePath);

            IntBuffer channelsBuffer = stack.mallocInt(1);
            IntBuffer sampleRateBuffer = stack.mallocInt(1);

            ShortBuffer rawAudioBuffer = stb_vorbis_decode_memory(
                    audioData, channelsBuffer, sampleRateBuffer);

            if (rawAudioBuffer == null) {
                throw new RuntimeException("Failed to decode OGG file: " + filePath.getFileName());
            }

            int channels = channelsBuffer.get();
            int sampleRate = sampleRateBuffer.get();

            int format = (channels == 1) ? AL_FORMAT_MONO16 : AL_FORMAT_STEREO16;

            int bufferId = alGenBuffers();
            alBufferData(bufferId, format, rawAudioBuffer, sampleRate);

            // Free the native memory
            MemoryUtil.memFree(audioData);

            return new AudioBuffer(bufferId, format, sampleRate, channels);

        } catch (Exception e) {
            System.err.println("Failed to load OGG from path: " + filePath.getFileName() + " - " + e.getMessage());
            return null;
        }
    }

    /**
     * Load file data from a Path (for preloading)
     */
    private ByteBuffer loadFileFromPath(Path filePath) throws IOException {
        byte[] audioBytes = Files.readAllBytes(filePath);
        
        ByteBuffer buffer = MemoryUtil.memAlloc(audioBytes.length);
        buffer.put(audioBytes);
        buffer.flip();
        return buffer;
    }

    /**
     * Load an audio file and return its buffer ID
     * Now checks preloaded buffers first!
     */
    public AudioBuffer loadAudio(String filename) {
        // Check if already preloaded
        if (loadedBuffers.containsKey(filename)) {
            return loadedBuffers.get(filename);
        }

        // Fallback to on-demand loading if not preloaded
        System.out.println("Loading on-demand (not preloaded): " + filename);
        
        AudioBuffer buffer = null;
        try {
            if (filename.toLowerCase().endsWith(".ogg")) {
                buffer = loadOGG(filename);
            } else if (filename.toLowerCase().endsWith(".wav")) {
                buffer = loadWAV(filename);
            } else {
                throw new RuntimeException("Unsupported audio format: " + filename);
            }

            if (buffer != null) {
                loadedBuffers.put(filename, buffer);
                System.out.println("Loaded audio on-demand: " + filename);
            }
            return buffer;

        } catch (Exception e) {
            System.err.println("Failed to load audio: " + filename + " - " + e.getMessage());
            return null;
        }
    }

    /**
     * Play a sound effect
     */
    public void playSFX(String filename) {
        playSFX(filename, 1.0f, 1.0f, false);
    }

    public void playSFX(String filename, float volume, float pitch, boolean loop) {
        AudioBuffer buffer = loadAudio(filename);
        if (buffer == null)
            return;

        // Find or create a source for this SFX
        int source = getAvailableSFXSource();

        alSourcei(source, AL_BUFFER, buffer.bufferId);
        alSourcef(source, AL_GAIN, volume * sfxVolume * masterVolume);
        alSourcef(source, AL_PITCH, pitch);
        alSourcei(source, AL_LOOPING, loop ? AL_TRUE : AL_FALSE);

        // Position at listener for 2D sound
        alSource3f(source, AL_POSITION, 0, 0, 0);

        alSourcePlay(source);
    }

    /**
     * Play music with intro/loop support
     * Now uses preloaded buffers for instant transitions!
     */
    public void playMusic(String introFile, String loopFile) {
        stopMusic();

        currentMusicIntro = introFile;
        currentMusicLoop = loopFile;
        introFinished = false;

        if (introFile != null) {
            currentMusic = introFile;
            playMusicFile(introFile);
        } else {
            currentMusic = loopFile;
            playMusicFile(loopFile);
            introFinished = true;
        }
    }

    public void playMusic(String musicFile) {
        playMusic(null, musicFile);
    }

    private void playMusicFile(String filename) {
        AudioBuffer buffer = loadAudio(filename);
        if (buffer == null)
            return;

        int source = getMusicSource();

        alSourcei(source, AL_BUFFER, buffer.bufferId);
        alSourcef(source, AL_GAIN, musicVolume * masterVolume);
        alSourcef(source, AL_PITCH, 1.0f);
        alSourcei(source, AL_LOOPING, introFinished ? AL_TRUE : AL_FALSE);

        alSourcePlay(source);
    }

    /**
     * Update the audio system - call this every frame
     * Now has instant loop transitions!
     */
    public void update() {
        // Check if intro music finished
        if (!introFinished && currentMusicIntro != null && currentMusicLoop != null) {
            int source = getMusicSource();
            int state = alGetSourcei(source, AL_SOURCE_STATE);

            if (state == AL_STOPPED) {
                // Intro finished, start loop instantly (buffer already loaded!)
                introFinished = true;
                currentMusic = currentMusicLoop;
                playMusicFile(currentMusicLoop);
            }
        }

        // Clean up finished SFX sources
        cleanupFinishedSFX();
    }

    /**
     * Stop all music
     */
    public void stopMusic() {
        if (!musicSources.isEmpty()) {
            int source = getMusicSource();
            alSourceStop(source);
        }
        currentMusic = null;
        currentMusicIntro = null;
        currentMusicLoop = null;
        introFinished = false;
    }

    /**
     * Stop all sound effects
     */
    public void stopAllSFX() {
        for (int source : sfxSources.values()) {
            alSourceStop(source);
        }
    }
    
    /**
     * Stop all audio
     */
    public void stopAll() {
        stopMusic();
        stopAllSFX();
    }

    // Volume controls
    public void setMasterVolume(float volume) {
        this.masterVolume = Math.max(0.0f, Math.min(1.0f, volume));
    }

    public void setMusicVolume(float volume) {
        this.musicVolume = Math.max(0.0f, Math.min(1.0f, volume));
        if (!musicSources.isEmpty()) {
            int source = getMusicSource();
            alSourcef(source, AL_GAIN, musicVolume * masterVolume);
        }
    }

    public void setSFXVolume(float volume) {
        this.sfxVolume = Math.max(0.0f, Math.min(1.0f, volume));
    }

    /**
     * Get list of preloaded audio files
     */
    public Set<String> getPreloadedFiles() {
        return new HashSet<>(loadedBuffers.keySet());
    }

    /**
     * Check if a specific file is preloaded
     */
    public boolean isPreloaded(String filename) {
        return loadedBuffers.containsKey(filename);
    }

    // Helper methods
    private int getMusicSource() {
        return musicSources.computeIfAbsent("music", k -> alGenSources());
    }

    private int getAvailableSFXSource() {
        // Find an available SFX source or create a new one
        for (Map.Entry<String, Integer> entry : sfxSources.entrySet()) {
            int source = entry.getValue();
            int state = alGetSourcei(source, AL_SOURCE_STATE);
            if (state != AL_PLAYING) {
                return source;
            }
        }

        // Create new source
        String sourceKey = "sfx_" + sfxSources.size();
        int source = alGenSources();
        sfxSources.put(sourceKey, source);
        return source;
    }

    private void cleanupFinishedSFX() {
        // Remove sources that are no longer playing
        sfxSources.entrySet().removeIf(entry -> {
            int source = entry.getValue();
            int state = alGetSourcei(source, AL_SOURCE_STATE);
            if (state == AL_STOPPED) {
                alDeleteSources(source);
                return true;
            }
            return false;
        });
    }

    private AudioBuffer loadOGG(String filename) {
        try (MemoryStack stack = stackPush()) {
            // Load OGG file
            ByteBuffer audioData = loadFileToBuffer(filename);

            IntBuffer channelsBuffer = stack.mallocInt(1);
            IntBuffer sampleRateBuffer = stack.mallocInt(1);

            ShortBuffer rawAudioBuffer = stb_vorbis_decode_memory(
                    audioData, channelsBuffer, sampleRateBuffer);

            if (rawAudioBuffer == null) {
                throw new RuntimeException("Failed to load OGG file: " + filename);
            }

            int channels = channelsBuffer.get();
            int sampleRate = sampleRateBuffer.get();

            int format = (channels == 1) ? AL_FORMAT_MONO16 : AL_FORMAT_STEREO16;

            int bufferId = alGenBuffers();
            alBufferData(bufferId, format, rawAudioBuffer, sampleRate);

            // Free the native memory
            MemoryUtil.memFree(audioData);

            return new AudioBuffer(bufferId, format, sampleRate, channels);

        } catch (Exception e) {
            throw new RuntimeException("Failed to load OGG: " + filename, e);
        }
    }

    private AudioBuffer loadWAV(String filename) {
        // Implementation for WAV loading (can use existing Java Sound API or STB)
        throw new RuntimeException("WAV loading not implemented yet - use OGG format");
    }

    private ByteBuffer loadFileToBuffer(String filename) {
        try {
            byte[] audioBytes = Objects.requireNonNull(
                    getClass().getResourceAsStream("/sounds/" + filename)).readAllBytes();

            ByteBuffer buffer = MemoryUtil.memAlloc(audioBytes.length);
            buffer.put(audioBytes);
            buffer.flip();
            return buffer;

        } catch (Exception e) {
            throw new RuntimeException("Failed to load audio file: " + filename, e);
        }
    }

    /**
     * Clean up resources
     */
    public void dispose() {
        stopAll();

        // Delete all sources
        for (int source : musicSources.values()) {
            alDeleteSources(source);
        }
        for (int source : sfxSources.values()) {
            alDeleteSources(source);
        }

        // Delete all buffers
        for (AudioBuffer buffer : loadedBuffers.values()) {
            alDeleteBuffers(buffer.bufferId);
        }

        // Clean up OpenAL
        alcMakeContextCurrent(0);
        alcDestroyContext(context);
        alcCloseDevice(device);

        System.out.println("LWJGL Audio Manager disposed");
    }

    // Inner class for audio buffer info
    private static class AudioBuffer {
        final int bufferId;
        final int format;
        final int sampleRate;
        final int channels;

        AudioBuffer(int bufferId, int format, int sampleRate, int channels) {
            this.bufferId = bufferId;
            this.format = format;
            this.sampleRate = sampleRate;
            this.channels = channels;
        }
    }
}