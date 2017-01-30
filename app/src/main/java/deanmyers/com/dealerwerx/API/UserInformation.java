package deanmyers.com.dealerwerx.API;

import android.content.Intent;
import android.util.Base64;
import android.util.Base64InputStream;
import android.util.Base64OutputStream;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by mac3 on 2016-11-11.
 */

public class UserInformation implements Serializable{
    private int id;
    private String firstName;
    private String lastName;
    private String email;
    private String accessToken;
    private String phoneNumber;
    private boolean isAgent;

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public boolean getIsAgent() { return isAgent; }

    public UserInformation(int id, String firstName, String lastName, String email, String phoneNumber, boolean isAgent, String accessToken) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.isAgent = isAgent;
        this.accessToken = accessToken;
    }

    public String toBase64String() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);

        oos.writeObject(this);

        String returnString = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);

        oos.close();
        baos.close();

        return  returnString;
    }

    public JSONObject toJsonObject() throws JSONException{
        JSONObject returnObject = new JSONObject();

        returnObject.put("firstName", firstName);
        returnObject.put("lastName", lastName);
        returnObject.put("email", email);
        returnObject.put("phone_number", phoneNumber == null ? JSONObject.NULL : phoneNumber);

        returnObject.put("access-token", accessToken);

        return returnObject;
    }

    public static UserInformation fromJsonObject(JSONObject obj) throws JSONException {
        return new UserInformation(
                obj.getInt("id"),
                obj.getString("firstName"),
                obj.getString("lastName"),
                obj.getString("email"),
                obj.isNull("phone_number") ? null : obj.getString("phone_number"),
                obj.getBoolean("isAgent"),
                obj.getString("access-token")
        );
    }

    public static UserInformation fromBase64String(String data) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bais = new ByteArrayInputStream(Base64.decode(data, Base64.DEFAULT));
        ObjectInputStream ois = new ObjectInputStream(bais);

        UserInformation returnInformation = (UserInformation)ois.readObject();

        ois.close();
        bais.close();

        return returnInformation;
    }
}
