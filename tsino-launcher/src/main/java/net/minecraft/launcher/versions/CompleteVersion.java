package net.minecraft.launcher.versions;

import java.io.File;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.launcher.OperatingSystem;
import net.minecraft.launcher.updater.download.Downloadable;

public class CompleteVersion implements Version {
	private String id;
	private Date time;
	private ReleaseType type;
	private String minecraftArguments;
	private List<Library> libraries;
	private String mainClass;
	private int minimumLauncherVersion;
	private String incompatibilityReason;
	private List<Rule> rules;
	private volatile boolean synced = false;

	public CompleteVersion() {
	}

	public CompleteVersion(CompleteVersion version) {
		this(version.getId(), version
				.getUpdatedTime(), version.getType(), version.getMainClass(),
				version.getMinecraftArguments());
	}

	public CompleteVersion(String id, Date updateTime,
			ReleaseType type, String mainClass, String minecraftArguments) {
		if ((id == null) || (id.length() == 0))
			throw new IllegalArgumentException("ID cannot be null or empty");
		if (updateTime == null)
			throw new IllegalArgumentException("Update time cannot be null");
		if (type == null)
			throw new IllegalArgumentException("Release type cannot be null");
		if ((mainClass == null) || (mainClass.length() == 0))
			throw new IllegalArgumentException(
					"Main class cannot be null or empty");
		if (minecraftArguments == null)
			throw new IllegalArgumentException(
					"Process arguments cannot be null or empty");

		this.id = id;
		this.time = updateTime;
		this.type = type;
		this.mainClass = mainClass;
		this.libraries = new ArrayList<Library>();
		this.minecraftArguments = minecraftArguments;
	}

	public CompleteVersion(Version version, String mainClass,
			String minecraftArguments) {
		this(version.getId(), version
				.getUpdatedTime(), version.getType(), mainClass,
				minecraftArguments);
	}

	public boolean appliesToCurrentEnvironment() {
		if (this.rules == null)
			return true;
		Rule.Action lastAction = Rule.Action.DISALLOW;

		for (Rule rule : this.rules) {
			Rule.Action action = rule.getAppliedAction();
			if (action != null)
				lastAction = action;
		}

		return lastAction == Rule.Action.ALLOW;
	}

	public Collection<File> getClassPath(OperatingSystem os, File base) {
		Collection<Library> libraries = getRelevantLibraries();
		Collection<File> result = new ArrayList<File>();

		for (Library library : libraries) {
			if (library.getNatives() == null) {
				result.add(new File(base, "libraries/"
						+ library.getArtifactPath()));
			}
		}

		result.add(new File(base, "versions/" + getId() + "/" + getId()
				+ ".jar"));

		return result;
	}

	public Collection<String> getExtractFiles(OperatingSystem os) {
		Collection<Library> libraries = getRelevantLibraries();
		Collection<String> result = new ArrayList<String>();

		for (Library library : libraries) {
			Map<OperatingSystem, String> natives = library.getNatives();

			if ((natives != null) && (natives.containsKey(os))) {
				result.add("libraries/"
						+ library.getArtifactPath(natives.get(os)));
			}
		}

		return result;
	}

	@Override
	public String getId() {
		return this.id;
	}

	public String getIncompatibilityReason() {
		return this.incompatibilityReason;
	}

	public Collection<Library> getLibraries() {
		return this.libraries;
	}

	public String getMainClass() {
		return this.mainClass;
	}

	public String getMinecraftArguments() {
		return this.minecraftArguments;
	}

	public int getMinimumLauncherVersion() {
		return this.minimumLauncherVersion;
	}

	public Collection<Library> getRelevantLibraries() {
		List<Library> result = new ArrayList<Library>();

		for (Library library : this.libraries) {
			if (library.appliesToCurrentEnvironment()) {
				result.add(library);
			}
		}

		return result;
	}

	public Set<Downloadable> getRequiredDownloadables(OperatingSystem os,
			Proxy proxy, File targetDirectory, boolean ignoreLocalFiles)
			throws MalformedURLException {
		Set<Downloadable> neededFiles = new HashSet<Downloadable>();

		for (Library library : getRelevantLibraries()) {
			String file = null;

			if (library.getNatives() != null) {
				String natives = library.getNatives().get(os);
				if (natives != null)
					file = library.getArtifactPath(natives);
			} else {
				file = library.getArtifactPath();
			}

			if (file != null) {
				URL url = new URL(library.getDownloadUrl() + file);
				File local = new File(targetDirectory, "libraries/" + file);

				if ((!local.isFile()) || (!library.hasCustomUrl())) {
					neededFiles.add(new Downloadable(proxy, url, local,
							ignoreLocalFiles));
				}
			}
		}

		return neededFiles;
	}

	public Set<String> getRequiredFiles(OperatingSystem os) {
		Set<String> neededFiles = new HashSet<String>();

		for (Library library : getRelevantLibraries()) {
			if (library.getNatives() != null) {
				String natives = library.getNatives().get(os);
				if (natives != null)
					neededFiles.add("libraries/"
							+ library.getArtifactPath(natives));
			} else {
				neededFiles.add("libraries/" + library.getArtifactPath());
			}

		}

		return neededFiles;
	}

	@Override
	public ReleaseType getType() {
		return this.type;
	}

	@Override
	public Date getUpdatedTime() {
		return this.time;
	}

	public boolean isSynced() {
		return this.synced;
	}

	public void setMainClass(String mainClass) {
		if ((mainClass == null) || (mainClass.length() == 0))
			throw new IllegalArgumentException(
					"Main class cannot be null or empty");
		this.mainClass = mainClass;
	}

	public void setMinecraftArguments(String minecraftArguments) {
		if (minecraftArguments == null)
			throw new IllegalArgumentException(
					"Process arguments cannot be null or empty");
		this.minecraftArguments = minecraftArguments;
	}

	public void setMinimumLauncherVersion(int minimumLauncherVersion) {
		this.minimumLauncherVersion = minimumLauncherVersion;
	}

	public void setSynced(boolean synced) {
		this.synced = synced;
	}

	@Override
	public void setType(ReleaseType type) {
		if (type == null)
			throw new IllegalArgumentException("Release type cannot be null");
		this.type = type;
	}

	@Override
	public void setUpdatedTime(Date time) {
		if (time == null)
			throw new IllegalArgumentException("Time cannot be null");
		this.time = time;
	}

	@Override
	public String toString() {
		return "CompleteVersion{id='" + this.id + '\'' + ", time=" + this.time
				+ ", type=" + this.type + ", libraries=" + this.libraries
				+ ", mainClass='" + this.mainClass + '\''
				+ ", minimumLauncherVersion=" + this.minimumLauncherVersion
				+ '}';
	}
}

/*
 * Location: Z:\home\vadim\.minecraft\launcher.jar Qualified Name:
 * net.minecraft.launcher.versions.CompleteVersion JD-Core Version: 0.6.2
 */