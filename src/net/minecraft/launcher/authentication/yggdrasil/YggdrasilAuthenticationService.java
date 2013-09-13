package net.minecraft.launcher.authentication.yggdrasil;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;

import net.minecraft.hopper.Util;
import net.minecraft.launcher.Launcher;
import net.minecraft.launcher.authentication.BaseAuthenticationService;
import net.minecraft.launcher.authentication.GameProfile;
import net.minecraft.launcher.authentication.exceptions.AuthenticationException;
import net.minecraft.launcher.authentication.exceptions.InvalidCredentialsException;
import net.minecraft.launcher.authentication.exceptions.UserMigratedException;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;

public class YggdrasilAuthenticationService extends BaseAuthenticationService {
	private static final String BASE_URL = "https://authserver.mojang.com/";
	private static final URL ROUTE_AUTHENTICATE = Util
			.constantURL("https://authserver.mojang.com/authenticate");
	private static final URL ROUTE_REFRESH = Util
			.constantURL("https://authserver.mojang.com/refresh");
	private static final URL ROUTE_VALIDATE = Util
			.constantURL("https://authserver.mojang.com/validate");
	private static final URL ROUTE_INVALIDATE = Util
			.constantURL("https://authserver.mojang.com/invalidate");
	private static final URL ROUTE_SIGNOUT = Util
			.constantURL("https://authserver.mojang.com/signout");
	private static final String STORAGE_KEY_ACCESS_TOKEN = "accessToken";
	private final Gson gson = new Gson();
	private final Agent agent = Agent.MINECRAFT;
	private GameProfile[] profiles;
	private String accessToken;
	private boolean isOnline;

	public boolean canLogIn() {
		return (!canPlayOnline())
				&& (StringUtils.isNotBlank(getUsername()))
				&& ((StringUtils.isNotBlank(getPassword())) || (StringUtils
						.isNotBlank(getAccessToken())));
	}

	public void logIn() throws AuthenticationException {
		if (StringUtils.isBlank(getUsername())) {
			throw new InvalidCredentialsException("Invalid username");
		}

		if (StringUtils.isNotBlank(getAccessToken()))
			logInWithToken();
		else if (StringUtils.isNotBlank(getPassword()))
			logInWithPassword();
		else
			throw new InvalidCredentialsException("Invalid password");
	}

	protected void logInWithPassword() throws AuthenticationException {
		if (StringUtils.isBlank(getUsername())) {
			throw new InvalidCredentialsException("Invalid username");
		}
		if (StringUtils.isBlank(getPassword())) {
			throw new InvalidCredentialsException("Invalid password");
		}

		Launcher.getInstance().println("Logging in with username & password");

		AuthenticationRequest request = new AuthenticationRequest(this,
				getPassword());
		AuthenticationResponse response = (AuthenticationResponse) makeRequest(
				ROUTE_AUTHENTICATE, request, AuthenticationResponse.class);

		if (!response.getClientToken().equals(getClientToken())) {
			throw new AuthenticationException(
					"Server requested we change our client token. Don't know how to handle this!");
		}

		this.accessToken = response.getAccessToken();
		this.profiles = response.getAvailableProfiles();
		setSelectedProfile(response.getSelectedProfile());

		fireAuthenticationChangedEvent();
	}

	protected void logInWithToken() throws AuthenticationException {
		if (StringUtils.isBlank(getUsername())) {
			throw new InvalidCredentialsException("Invalid username");
		}
		if (StringUtils.isBlank(getAccessToken())) {
			throw new InvalidCredentialsException("Invalid access token");
		}

		Launcher.getInstance().println("Logging in with access token");

		RefreshRequest request = new RefreshRequest(this);
		RefreshResponse response = (RefreshResponse) makeRequest(ROUTE_REFRESH,
				request, RefreshResponse.class);

		if (!response.getClientToken().equals(getClientToken())) {
			throw new AuthenticationException(
					"Server requested we change our client token. Don't know how to handle this!");
		}

		this.accessToken = response.getAccessToken();
		this.profiles = response.getAvailableProfiles();
		setSelectedProfile(response.getSelectedProfile());

		fireAuthenticationChangedEvent();
	}

	protected <T extends Response> T makeRequest(URL url, Object input,
			Class<T> classOfT) throws AuthenticationException {
		try {
			String jsonResult = Util
					.performPost(url, this.gson.toJson(input), Launcher
							.getInstance().getProxy(), "application/json", true);
			T result = (T) this.gson.fromJson(jsonResult, classOfT);

			if (result == null)
				return null;

			if (StringUtils.isNotBlank(result.getError())) {
				if ("UserMigratedException".equals(result.getCause()))
					throw new UserMigratedException(result.getErrorMessage());
				if (result.getError().equals("ForbiddenOperationException")) {
					throw new InvalidCredentialsException(
							result.getErrorMessage());
				}
				throw new AuthenticationException(result.getErrorMessage());
			}

			this.isOnline = true;

			return result;
		} catch (IOException e) {
			throw new AuthenticationException(
					"Cannot contact authentication server", e);
		} catch (IllegalStateException e) {
			throw new AuthenticationException(
					"Cannot contact authentication server", e);
		} catch (JsonParseException e) {
			throw new AuthenticationException(
					"Cannot contact authentication server", e);
		}
	}

	public void logOut() {
		super.logOut();

		this.accessToken = null;
		this.profiles = null;
		this.isOnline = false;
	}

	public GameProfile[] getAvailableProfiles() {
		return this.profiles;
	}

	public boolean isLoggedIn() {
		return StringUtils.isNotBlank(this.accessToken);
	}

	public boolean canPlayOnline() {
		return (isLoggedIn()) && (getSelectedProfile() != null)
				&& (this.isOnline);
	}

	public void selectGameProfile(GameProfile profile)
			throws AuthenticationException {
		if (!isLoggedIn()) {
			throw new AuthenticationException(
					"Cannot change game profile whilst not logged in");
		}
		if (getSelectedProfile() != null) {
			throw new AuthenticationException(
					"Cannot change game profile. You must log out and back in.");
		}
		if ((profile == null) || (!ArrayUtils.contains(this.profiles, profile))) {
			throw new IllegalArgumentException("Invalid profile '" + profile
					+ "'");
		}

		RefreshRequest request = new RefreshRequest(this, profile);
		RefreshResponse response = (RefreshResponse) makeRequest(ROUTE_REFRESH,
				request, RefreshResponse.class);

		if (!response.getClientToken().equals(getClientToken())) {
			throw new AuthenticationException(
					"Server requested we change our client token. Don't know how to handle this!");
		}

		this.accessToken = response.getAccessToken();
		setSelectedProfile(response.getSelectedProfile());

		fireAuthenticationChangedEvent();
	}

	public void loadFromStorage(Map<String, String> credentials) {
		super.loadFromStorage(credentials);

		this.accessToken = ((String) credentials.get("accessToken"));
	}

	public Map<String, String> saveForStorage() {
		Map<String, String> result = super.saveForStorage();
		if (!shouldRememberMe())
			return result;

		if (StringUtils.isNotBlank(getAccessToken())) {
			result.put("accessToken", getAccessToken());
		}

		return result;
	}

	public String getSessionToken() {
		if ((isLoggedIn()) && (getSelectedProfile() != null)
				&& (canPlayOnline())) {
			return String.format("token:%s:%s", new Object[] {
					getAccessToken(), getSelectedProfile().getId() });
		}
		return null;
	}

	public String getAccessToken() {
		return this.accessToken;
	}

	public String getClientToken() {
		return Launcher.getInstance().getClientToken().toString();
	}

	public Agent getAgent() {
		return this.agent;
	}

	public String toString() {
		return "YggdrasilAuthenticationService{agent=" + this.agent
				+ ", profiles=" + Arrays.toString(this.profiles)
				+ ", selectedProfile=" + getSelectedProfile()
				+ ", sessionToken='" + getSessionToken() + '\''
				+ ", username='" + getUsername() + '\'' + ", isLoggedIn="
				+ isLoggedIn() + ", canPlayOnline=" + canPlayOnline()
				+ ", accessToken='" + this.accessToken + '\''
				+ ", clientToken='" + getClientToken() + '\'' + '}';
	}
}

/*
 * Location: Z:\home\vadim\.minecraft\launcher.jar Qualified Name:
 * net.minecraft.
 * launcher.authentication.yggdrasil.YggdrasilAuthenticationService JD-Core
 * Version: 0.6.2
 */