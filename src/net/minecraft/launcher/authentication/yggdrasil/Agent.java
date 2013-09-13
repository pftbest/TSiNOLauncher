package net.minecraft.launcher.authentication.yggdrasil;

public class Agent {
	public static final Agent MINECRAFT = new Agent("Minecraft", 1);
	private final String name;
	private final int version;

	public Agent(String name, int version) {
		this.name = name;
		this.version = version;
	}

	public String getName() {
		return this.name;
	}

	public int getVersion() {
		return this.version;
	}

	public String toString() {
		return "Agent{name='" + this.name + '\'' + ", version=" + this.version
				+ '}';
	}
}

/*
 * Location: Z:\home\vadim\.minecraft\launcher.jar Qualified Name:
 * net.minecraft.launcher.authentication.yggdrasil.Agent JD-Core Version: 0.6.2
 */