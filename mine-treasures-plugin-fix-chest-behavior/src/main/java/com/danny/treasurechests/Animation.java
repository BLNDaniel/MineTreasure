package com.danny.treasurechests;

import org.bukkit.Particle;

import java.util.List;

public class Animation {

    public static class AnimationInfo {
        private final SoundEffect sound;
        private final List<ParticleEffect> particles;
        private final ScaleEffect scale;


        public AnimationInfo(SoundEffect sound, List<ParticleEffect> particles, ScaleEffect scale) {
            this.sound = sound;
            this.particles = particles;
            this.scale = scale;
        }

        public SoundEffect getSound() { return sound; }
        public List<ParticleEffect> getParticles() { return particles; }
        public ScaleEffect getScale() { return scale; }
    }

    public static class SoundEffect {
        private final String name;
        private final float volume;
        private final float pitch;

        public SoundEffect(String name, float volume, float pitch) {
            this.name = name;
            this.volume = volume;
            this.pitch = pitch;
        }

        public String getName() { return name; }
        public float getVolume() { return volume; }
        public float getPitch() { return pitch; }
    }

    public static class ParticleEffect {
        private final Particle type;
        private final int count;
        private final double speed;

        public ParticleEffect(Particle type, int count, double speed) {
            this.type = type;
            this.count = count;
            this.speed = speed;
        }

        public Particle getType() { return type; }
        public int getCount() { return count; }
        public double getSpeed() { return speed; }
    }

    public static class ScaleEffect {
        private final double from;
        private final double to;
        private final int duration;

        public ScaleEffect(double from, double to, int duration) {
            this.from = from;
            this.to = to;
            this.duration = duration;
        }

        public double getFrom() { return from; }
        public double getTo() { return to; }
        public int getDuration() { return duration; }
    }
}
