import java.util.Vector;


public class Yonghu {
   String name;
   String passward;
   int number;


Vector<Integer> friends;
   public Yonghu(int number,String name, String passward,Vector<Integer> friends) {
	   this.number=number;
		this.name = name;
	    this.passward = passward;
	    this.friends=friends;
   }
   public Vector<Integer> getFriends() {
		return friends;
	}
   public String getName() {
	    return name;
    }

    public void setName(String name) {
	    this.name = name;
    }
    public int getNumber(){
    	return number;
    }
    public String getPassward() {
	    return passward;
    }

    public void setPassward(String passward) {
	    this.passward = passward;
    }
   public void addFriends(int friend){
	   friends.add(friend);
   }
   public void deleteFriends(int friend){
	   friends.remove(friend);
   }

	
}
