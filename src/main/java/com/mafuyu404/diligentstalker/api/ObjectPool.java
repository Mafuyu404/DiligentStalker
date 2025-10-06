package com.mafuyu404.diligentstalker.api;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec3;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class ObjectPool {
    private static final ConcurrentLinkedQueue<MutableVec3> VEC3_POOL = new ConcurrentLinkedQueue<>();
    private static final ConcurrentLinkedQueue<CompoundTag> TAG_POOL = new ConcurrentLinkedQueue<>();
    private static final AtomicInteger VEC3_POOL_SIZE = new AtomicInteger(0);
    private static final AtomicInteger TAG_POOL_SIZE = new AtomicInteger(0);
    private static final int MAX_POOL_SIZE = 1000;

    public static class MutableVec3 {
        public double x, y, z;

        public MutableVec3 set(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
            return this;
        }

        public MutableVec3 set(Vec3 vec) {
            return set(vec.x, vec.y, vec.z);
        }

        public Vec3 toVec3() {
            return new Vec3(x, y, z);
        }

        public MutableVec3 add(MutableVec3 other) {
            this.x += other.x;
            this.y += other.y;
            this.z += other.z;
            return this;
        }

        public MutableVec3 subtract(MutableVec3 other) {
            this.x -= other.x;
            this.y -= other.y;
            this.z -= other.z;
            return this;
        }

        public double length() {
            return Math.sqrt(x * x + y * y + z * z);
        }

        public MutableVec3 normalize() {
            double len = length();
            if (len > 1e-6) {
                x /= len;
                y /= len;
                z /= len;
            }
            return this;
        }
    }

    public static MutableVec3 getMutableVec3() {
        MutableVec3 vec = VEC3_POOL.poll();
        if (vec == null) {
            vec = new MutableVec3();
        } else {
            VEC3_POOL_SIZE.decrementAndGet();
        }
        return vec;
    }

    public static void returnMutableVec3(MutableVec3 vec) {
        if (VEC3_POOL_SIZE.get() < MAX_POOL_SIZE) {
            VEC3_POOL.offer(vec);
            VEC3_POOL_SIZE.incrementAndGet();
        }
    }

    public static CompoundTag getCompoundTag() {
        CompoundTag tag = TAG_POOL.poll();
        if (tag == null) {
            tag = new CompoundTag();
        } else {
            tag.getAllKeys().clear();
            TAG_POOL_SIZE.decrementAndGet();
        }
        return tag;
    }

    public static void returnCompoundTag(CompoundTag tag) {
        if (TAG_POOL_SIZE.get() < MAX_POOL_SIZE) {
            TAG_POOL.offer(tag);
            TAG_POOL_SIZE.incrementAndGet();
        }
    }
}