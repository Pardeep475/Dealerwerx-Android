package deanmyers.com.dealerwerx;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.ArrayAdapter;

import org.json.JSONException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Locale;

import deanmyers.com.dealerwerx.API.UserInformation;

/**
 * Created by mac3 on 2016-11-16.
 */

public class PreferencesManager {
    private static final String PREF_KEY = "{0000-0000-0000-0000}";
    private static final String USERINFORMATION_KEY = "USERINFORMATION";
    private static final String BACKGROUNDSCANNING_KEY = "BACKGROUNDSCANNING";
    private static final String LIKEDLIST_KEYFORMAT = "LIKEDLIST_%d";
    private static final String BEACON_LOOKING_MODE_KEY = "BEACONLOOKINGMODE";

    private static UserInformation userInfo;

    private static SharedPreferences getPreferences(){
        return DealerwerxApplication.getContext().getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);
    }

    public static boolean hasUserInformation(){
        return getUserInformation() != null;
    }

    public static UserInformation getUserInformation(){
        SharedPreferences preferences = getPreferences();
        try{
            if(userInfo == null && preferences.contains(USERINFORMATION_KEY))
                userInfo = UserInformation.fromJsonString(preferences.getString(USERINFORMATION_KEY, ""));
        }catch(JSONException e){
        }
        finally {
            return userInfo;
        }
    }

    public static boolean setUserInformation(UserInformation info){
        SharedPreferences.Editor editor = getPreferences().edit();

        try{
            if(info != null)
                editor.putString(USERINFORMATION_KEY, info.toJsonString());
            else
                editor.remove(USERINFORMATION_KEY);

            editor.apply();
        }catch(JSONException e){
            return false;
        }

        userInfo = info;
        return true;
    }

    public static boolean getAllowBackgroundScanning(){
        SharedPreferences preferences = getPreferences();
        return preferences.getBoolean(BACKGROUNDSCANNING_KEY, true);
    }

    public static void setAllowBackgroundScanning(boolean allow){
        SharedPreferences.Editor editor = getPreferences().edit();

        editor.putBoolean(BACKGROUNDSCANNING_KEY, allow);

        editor.apply();
    }

    public static boolean getBeaconLookingMode(){
        SharedPreferences preferences = getPreferences();
        return preferences.getBoolean(BEACON_LOOKING_MODE_KEY, false);
    }

    public static void setBeaconLookingMode(boolean allow){
        SharedPreferences.Editor editor = getPreferences().edit();

        editor.putBoolean(BEACON_LOOKING_MODE_KEY, allow);

        editor.apply();
    }

    @SuppressWarnings("unchecked")
    private static ArrayList<Integer> getLikedList(){
        ArrayList<Integer> likedList = new ArrayList<>();
        SharedPreferences preferences = getPreferences();
        try{
            Object obj = ObjectSerializer.deserialize(preferences.getString(String.format(Locale.CANADA, LIKEDLIST_KEYFORMAT, getUserInformation().getId()), ""));
            if(obj != null)
                likedList = (ArrayList<Integer>)(obj);
        }catch(Exception e){
            likedList = new ArrayList<>();
        }
        finally {
            return likedList;
        }
    }

    public static void addLike(int listingId){
        ArrayList<Integer> likedList = getLikedList();
        likedList.add(listingId);
        setLikedList(likedList);
    }

    public static void removeLike(int listingId){
        ArrayList<Integer> likedList = getLikedList();
        likedList.remove(likedList.indexOf(listingId));
        setLikedList(likedList);
    }

    public static boolean hasLiked(int listingId){
        ArrayList<Integer> likedList = getLikedList();
        return likedList.contains(listingId);
    }

    private static boolean setLikedList(ArrayList<Integer> info){
        SharedPreferences.Editor editor = getPreferences().edit();

        try{
            if(info != null)
                editor.putString(String.format(Locale.CANADA, LIKEDLIST_KEYFORMAT, getUserInformation().getId()), ObjectSerializer.serialize(info));
            else
                editor.remove(USERINFORMATION_KEY);

            editor.apply();
        }catch(IOException e){
            return false;
        }

        return true;
    }

    private static class ObjectSerializer {
        public static String serialize(Serializable obj) throws IOException {
            if (obj == null) return "";
            try {
                ByteArrayOutputStream serialObj = new ByteArrayOutputStream();
                ObjectOutputStream objStream = new ObjectOutputStream(serialObj);
                objStream.writeObject(obj);
                objStream.close();
                return encodeBytes(serialObj.toByteArray());
            } catch (Exception e) {
                return null;
            }
        }

        public static Object deserialize(String str) throws IOException {
            if (str == null || str.length() == 0) return null;
            try {
                ByteArrayInputStream serialObj = new ByteArrayInputStream(decodeBytes(str));
                ObjectInputStream objStream = new ObjectInputStream(serialObj);
                return objStream.readObject();
            } catch (Exception e) {
                return null;
            }
        }

        public static String encodeBytes(byte[] bytes) {
            StringBuffer strBuf = new StringBuffer();

            for (int i = 0; i < bytes.length; i++) {
                strBuf.append((char) (((bytes[i] >> 4) & 0xF) + ((int) 'a')));
                strBuf.append((char) (((bytes[i]) & 0xF) + ((int) 'a')));
            }

            return strBuf.toString();
        }

        public static byte[] decodeBytes(String str) {
            byte[] bytes = new byte[str.length() / 2];
            for (int i = 0; i < str.length(); i+=2) {
                char c = str.charAt(i);
                bytes[i/2] = (byte) ((c - 'a') << 4);
                c = str.charAt(i+1);
                bytes[i/2] += (c - 'a');
            }
            return bytes;
        }

    }
}
