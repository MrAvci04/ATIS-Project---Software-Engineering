package gsix.ATIS.client.login;

import gsix.ATIS.client.SimpleClient;
import gsix.ATIS.entities.Message;
import gsix.ATIS.entities.User;

import java.io.IOException;
import java.time.LocalDateTime;

/*import client.CEMSClient;
import client.ClientUI;
import entity.Course;
import entity.Lecturer;
import entity.LoggedInUser;
import entity.Profession;
import entity.User;
import logic.Msg;
import logic.MsgType;
import entity.UserType;
import gui.GuiCommon;*/

public class LoginController {
	public static boolean withReq=true;
	private static boolean headDep= false;
	
	public static void setWithReq(boolean b) {
		withReq=b;
	}
	
	public static String loginUser(User userDetails) throws IOException {
		User user = userDetails;
		System.out.println("hey from LoginController");
		Message message = new Message(1, LocalDateTime.now(), "login request", user);
		try {
			SimpleClient.getClient("", 0).sendToServer(message);
		}catch (Exception e){

		}
		return null;

	}
	
	public static void loginLecturer(/*Lecturer lecturer */) throws Exception{
		/*Msg getProfessions = new Msg("getLecturerProfessions",MsgType.FROM_CLIENT,lecturer.getId());
		ClientUI.chat.accept(getProfessions);
		if(!CEMSClient.responseFromServer.getMsg().equals("getLecturerProfessions")) {
			throw new Exception("Recieved unexpected response from server (LoginFrame class)");
		}
		ArrayList<Profession> professions = (ArrayList<Profession>) CEMSClient.responseFromServer.getData();
		lecturer.setProfessions(professions);
		//Abdullha getCourses
		Msg getCourses = new Msg("getLecturerCourses",MsgType.FROM_CLIENT,professions);
		ClientUI.chat.accept(getCourses);
		ArrayList<Course> courses = (ArrayList<Course>) CEMSClient.responseFromServer.getData();
		lecturer.setLecturerCourses(courses);*/
	}
	
	
	public static void logOut() {
		/*User user = null;
		try {
			if(withReq && headDep){
				user=LoggedInUser.getHeadDep();
			}else {
				user=LoggedInUser.getInstance().getUser();
			}
			
		} catch (Exception e) {
		}
		if(user!=null) {
			if(withReq) {
				Msg msg = new Msg("logout",MsgType.FROM_CLIENT,user);
				ClientUI.chat.accept(msg);
				if(headDep) {
					LoggedInUser.getInstance().logOutHeadDep();
					headDep=false;
				}
			}
			
			LoggedInUser.getInstance().logOut();
		}
		else {
			System.out.println("User is null!");
		}*/
		
	}
	
	public static void disconnect() {
		/*logOut();
		String id;	
		String ipStr=null;
		try {
    		 InetAddress ip = InetAddress.getLocalHost();
             ipStr=ip.getHostAddress();
         } catch (UnknownHostException e) {
             e.printStackTrace();
         }
		if(withReq) {
			Msg msg1=new Msg("disconnect",MsgType.FROM_CLIENT,ipStr);
			ClientUI.chat.accept(msg1);//disconnect request
		}	*/
	}
	
	public static boolean logOutHeadDep() {
		/*try {
    		User user= LoggedInUser.getInstance().getUser();
    		//if gotten here user is logged in 
    		GuiCommon.getInstance().popUp("You must exit the View As window before logging out");
    		return false;
    	}catch(Exception e) {
		LoginController.setWithReq(true);
		LoginController.logOut();
		return true;
    	}*/
		return true;
	}
	
	
}
