package deanmyers.project.dealerwerx.API;

import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

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
    private String qrImageUrl;

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

    public String getQrImageUrl() {
        return qrImageUrl;
    }

    public boolean getIsAgent() { return isAgent; }

    public UserInformation(int id, String firstName, String lastName, String email, String phoneNumber, String qrImageUrl, boolean isAgent, String accessToken) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.qrImageUrl = qrImageUrl;
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

    public String toJsonString() throws JSONException {
        return toJsonObject().toString();
    }

    public JSONObject toJsonObject() throws JSONException{
        JSONObject returnObject = new JSONObject();

        returnObject.put("id", id);
        returnObject.put("firstName", firstName);
        returnObject.put("lastName", lastName);
        returnObject.put("email", email);
        returnObject.put("phone_number", phoneNumber == null ? JSONObject.NULL : phoneNumber);
        returnObject.put("qr_url", qrImageUrl == null ? JSONObject.NULL : qrImageUrl);
        returnObject.put("isAgent", isAgent);
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
                obj.isNull("qr_url") ? null : obj.getString("qr_url"),
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

    public static UserInformation fromJsonString(String data) throws JSONException {
        return fromJsonObject(new JSONObject(data));
    }
}
