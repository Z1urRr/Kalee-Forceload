package fun.kalee.forcedload.data;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class ChunkData {
    private final UUID ownerId;
    private final String worldName;
    private final int chunkX;
    private final int chunkZ;
    private final long purchaseTime;
    private final long expireTime;

    public ChunkData(UUID ownerId, String worldName, int chunkX, int chunkZ, long purchaseTime, long expireTime) {
        this.ownerId = ownerId;
        this.worldName = worldName;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.purchaseTime = purchaseTime;
        this.expireTime = expireTime;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public String getWorldName() {
        return worldName;
    }

    public int getChunkX() {
        return chunkX;
    }

    public int getChunkZ() {
        return chunkZ;
    }

    public long getPurchaseTime() {
        return purchaseTime;
    }

    public long getExpireTime() {
        return expireTime;
    }

    public String getPurchaseTimeFormatted() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date(purchaseTime));
    }

    public String getExpireTimeFormatted() {
        if (expireTime == 0) {
            return "永久";
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date(expireTime));
    }

    public boolean isExpired() {
        if (expireTime == 0) {
            return false;
        }
        return System.currentTimeMillis() > expireTime;
    }

    public String getChunkKey() {
        return worldName + ":" + chunkX + ":" + chunkZ;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ChunkData chunkData = (ChunkData) obj;
        return chunkX == chunkData.chunkX &&
               chunkZ == chunkData.chunkZ &&
               worldName.equals(chunkData.worldName);
    }

    @Override
    public int hashCode() {
        return getChunkKey().hashCode();
    }

    @Override
    public String toString() {
        return "ChunkData{" +
                "ownerId=" + ownerId +
                ", worldName='" + worldName + '\'' +
                ", chunkX=" + chunkX +
                ", chunkZ=" + chunkZ +
                ", purchaseTime=" + purchaseTime +
                ", expireTime=" + expireTime +
                '}';
    }
}

