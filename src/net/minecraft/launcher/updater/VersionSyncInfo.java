package net.minecraft.launcher.updater;

import net.minecraft.launcher.versions.Version;

public class VersionSyncInfo {
	public static enum VersionSource {
		REMOTE, LOCAL;
	}

	private final Version localVersion;
	private final Version remoteVersion;
	private final boolean isInstalled;

	private final boolean isUpToDate;

	public VersionSyncInfo(Version localVersion, Version remoteVersion,
			boolean installed, boolean upToDate) {
		this.localVersion = localVersion;
		this.remoteVersion = remoteVersion;
		this.isInstalled = installed;
		this.isUpToDate = upToDate;
	}

	public VersionSource getLatestSource() {
		if (getLocalVersion() == null)
			return VersionSource.REMOTE;
		if (getRemoteVersion() == null)
			return VersionSource.LOCAL;
		if (getRemoteVersion().getUpdatedTime().after(
				getLocalVersion().getUpdatedTime()))
			return VersionSource.REMOTE;
		return VersionSource.LOCAL;
	}

	public Version getLatestVersion() {
		if (getLatestSource() == VersionSource.REMOTE) {
			return this.remoteVersion;
		}
		return this.localVersion;
	}

	public Version getLocalVersion() {
		return this.localVersion;
	}

	public Version getRemoteVersion() {
		return this.remoteVersion;
	}

	public boolean isInstalled() {
		return this.isInstalled;
	}

	public boolean isOnRemote() {
		return this.remoteVersion != null;
	}

	public boolean isUpToDate() {
		return this.isUpToDate;
	}

	@Override
	public String toString() {
		return "VersionSyncInfo{localVersion=" + this.localVersion
				+ ", remoteVersion=" + this.remoteVersion + ", isInstalled="
				+ this.isInstalled + ", isUpToDate=" + this.isUpToDate + '}';
	}
}

/*
 * Location: Z:\home\vadim\.minecraft\launcher.jar Qualified Name:
 * net.minecraft.launcher.updater.VersionSyncInfo JD-Core Version: 0.6.2
 */