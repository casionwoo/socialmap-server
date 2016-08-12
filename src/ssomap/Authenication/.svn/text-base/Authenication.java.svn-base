package ssomap.Authenication;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class Authenication {
	private UserService userService = UserServiceFactory.getUserService();
	public User user = null;
	
	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public User getAuthentication()
	{
		User user = userService.getCurrentUser();
		if (user == null) {
			return null;
		}
		return user;
	}
}
