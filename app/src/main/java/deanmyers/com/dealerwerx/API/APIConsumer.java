package deanmyers.com.dealerwerx.API;

/**
 * Created by mac3 on 2016-11-11.
 */

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;


public final class APIConsumer {
    private static boolean TEST_LOCALHOST = false;

    private static final String API_PROTOCOL = "http";
    private static final String API_LOCALDOMAIN = "10.183.7.194";
    private static final String API_REMOTEDOMAIN = "dealerwerx-newpanel.us-east-1.elasticbeanstalk.com";
    private static final String API_DOMAIN = TEST_LOCALHOST ? API_LOCALDOMAIN : API_REMOTEDOMAIN;
    private static final String API_PATH = "/api/";
    private static final String API_ENDPOINT = API_PROTOCOL + "://" + API_DOMAIN + API_PATH;
    private static final String LISTINGS_SUFFIX = "listings";
    private static final String BEACONS_SUFFIX = "beacons";
    private static final String MYLISTINGS_SUFFIX = "listings/owned";
    private static final String MYBEACONS_SUFFIX = "beacons/owned";
    private static final String ADDBEACON_SUFFIX = "beacons/add";
    private static final String FROMBEACON_SUFFIX = "listings/frombeacon";
    private static final String LISTINGSDELETE_SUFFIX = "delete";
    private static final String SCAVENGERLISTINGS_SUFFIX = "scavengers";
    private static final String VINDECODE_SUFFIX = "vehicles/fromvin";
    private static final String REGISTER_SUFFIX = "register";
    private static final String RENAME_SUFFIX = "rename";
    private static final String ASSOCIATE_SUFFIX = "associate";
    private static final String PURCHASE_SUFFIX = "purchase";
    private static final String HOLD_SUFFIX = "hold";
    private static final String OFFER_SUFFIX = "offer";
    private static final String UPDATEUSER_SUFFIX = "updateuser";
    private static final String LOGIN_SUFFIX = "login";
    private static final String VALIDATE_SUFFIX = "validate";
    private static final String IMAGEUPLOAD_SUFFIX = "listings/images";
    private static final String LISTINGUPDATE_SUFFIX = "update";
    private static final String VIDEOS_SUFFIX = "videos";
    private static final String GEOCODE_URLFORMAT = "http://maps.google.com/maps/api/geocode/json?address=%s&sensor=false";

    private static final ConcurrentHashMap<String, APITaskResult<double[]>> geoMap = new ConcurrentHashMap<>();

    public static LoginAsyncTask Login(String email, String password, APIResponder<UserInformation> responder){
        return new LoginAsyncTask(email, password, responder);
    }

    public static RegisterAsyncTask Register(String firstName, String lastName, String email, String password, APIResponder<UserInformation> responder){
        return new RegisterAsyncTask(firstName, lastName, email, password, responder);
    }

    public static CreateListingAsyncTask CreateListing(String accessToken, Listing listing, APIResponder<Listing> responder){
        return new CreateListingAsyncTask(accessToken, listing, responder);
    }

    public static UpdateListingAsyncTask UpdateListing(String accessToken, int listingId, Listing listing, APIResponder<Listing> responder){
        return new UpdateListingAsyncTask(accessToken, listingId, listing, responder);
    }

    public static DeleteListingAsyncTask DeleteListing(String accessToken, Listing listing, APIResponder<Void> responder){
        return new DeleteListingAsyncTask(accessToken, listing, responder);
    }

    public static CreateScavengerListingAsyncTask CreateScavengerListing(String accessToken, Listing listing, APIResponder<Listing> responder){
        return new CreateScavengerListingAsyncTask(accessToken, listing, responder);
    }

    public static GetListingsAsyncTask GetListings(String accessToken, APIResponder<Listing[]> responder){
        return new GetListingsAsyncTask(accessToken, responder);
    }

    public static GetMyListingsAsyncTask GetMyListings(String accessToken, APIResponder<Listing[]> responder){
        return new GetMyListingsAsyncTask(accessToken, responder);
    }

    public static GetScavengerListingsAsyncTask GetScavengerListings(String accessToken, APIResponder<Listing[]> responder){
        return new GetScavengerListingsAsyncTask(accessToken, responder);
    }

    public static DownloadImageAsyncTask DownloadImage(String imageUrl, APIResponder<Bitmap> responder){
        return new DownloadImageAsyncTask(imageUrl, responder);
    }

    public static VinDecodeAsyncTask VinDecode(String accessToken, String vin, APIResponder<VinDecodeResult> responder){
        return new VinDecodeAsyncTask(accessToken, vin, responder);
    }

    public static UploadImageAsyncTask UploadImage(String accessToken, int listingId, Bitmap image, APIResponder<Void> responder){
        return new UploadImageAsyncTask(accessToken, listingId, image, responder);
    }

    public static ValidateTokenAsyncTask ValidateToken(String accessToken, APIResponder<UserInformation> responder){
        return new ValidateTokenAsyncTask(accessToken, responder);
    }

    public static UpdateUserAsyncTask UpdateUser(UserInformation userInfo, APIResponder<UserInformation> responder){
        return new UpdateUserAsyncTask(userInfo, responder);
    }

    public static GetVideosAsyncTask GetVideos(APIResponder<VideoEntry[]> responder){
        return new GetVideosAsyncTask(responder);
    }

    public static GetLatLonAsyncTask GetLatLon(String address, APIResponder<double[]> responder){
        return new GetLatLonAsyncTask(address, responder);
    }

    public static HoldListingAsyncTask HoldListing(String accessToken, int listingId, APIResponder<Void> responder){
        return new HoldListingAsyncTask(accessToken, listingId, responder);
    }

    public static PurchaseListingAsyncTask PurchaseListing(String accessToken, int listingId, APIResponder<Void> responder){
        return new PurchaseListingAsyncTask(accessToken, listingId, responder);
    }

    public static OfferOnListingAsyncTask OfferOnListing(String accessToken, int listingId, double offerPrice, APIResponder<Void> responder){
        return new OfferOnListingAsyncTask(accessToken, listingId, offerPrice, responder);
    }

    public static GetListingFromBeaconAsyncTask GetListingFromBeacon(String accessToken, String uuid, int major, int minor, APIResponder<Listing> responder){
        return new GetListingFromBeaconAsyncTask(accessToken, uuid, major, minor, responder);
    }

    public static GetMyBeaconsAsyncTask GetMyBeacons(String accessToken, APIResponder<Beacon[]> responder){
        return new GetMyBeaconsAsyncTask(accessToken, responder);
    }

    public static RenameBeaconAsyncTask RenameBeacon(String accessToken, Beacon beacon, String name, APIResponder<Beacon> responder){
        return new RenameBeaconAsyncTask(accessToken, beacon, name, responder);
    }

    public static AssociateBeaconAsyncTask AssociateBeacon(String accessToken, Beacon beacon, Listing listing, APIResponder<Beacon> responder){
        return new AssociateBeaconAsyncTask(accessToken, beacon, listing, responder);
    }

    public static AddBeaconAsyncTask AddBeacon(String accessToken, String code, APIResponder<Beacon> responder){
        return new AddBeaconAsyncTask(accessToken, code, responder);
    }

    private static class APITaskResult<T>{
        boolean success = false;
        T result = null;
        String errorMessage = "An unknown error has occurred";
    }

    public static abstract class APIAsyncTask<T> extends AsyncTask<Void, Void, APITaskResult<T>>{
        private APIResponder<T> responder;
        static final String CHARSET = "UTF-8";

        private static final String crlf = "\r\n";
        private static final String twoHyphens = "--";
        private static final String boundary =  "*****";

        APIAsyncTask(APIResponder<T> responder) {
            this.responder = responder;
        }

        static void setBearer(String accessToken, HttpURLConnection conn){
            conn.setRequestProperty("Authorization", "Bearer " + accessToken);
        }

        static void configurePost(HttpURLConnection conn) throws ProtocolException {
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type", "application/json; charset=" + CHARSET.toLowerCase());
            conn.setRequestProperty("Accept", "application/json; charset=" + CHARSET.toLowerCase());
            conn.setRequestProperty("Accept-charset", CHARSET.toLowerCase());
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestMethod("POST");
        }

        static void configureMultipart(HttpURLConnection conn) throws ProtocolException {
            conn.setUseCaches(false);
            conn.setDoOutput(true);

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Cache-Control", "no-cache");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
        }

        static void attachMultipartString(OutputStream os, String name, String data) throws IOException {
            DataOutputStream dos = new DataOutputStream(os);
            dos.writeBytes(twoHyphens + boundary + crlf);
            dos.writeBytes("Content-Type: text/plain" + crlf);
            dos.writeBytes("Content-Disposition: form-data; name=\"" +
                    name + "\"" + crlf);
            dos.writeBytes(crlf);
            dos.writeBytes(data);
            dos.writeBytes(crlf);
            dos.flush();
        }

        static void uploadAttachment(OutputStream os, byte[] data, String attachmentName, String attachmentFileName, String mimeType) throws IOException {
            DataOutputStream dos = new DataOutputStream(os);
            dos.writeBytes(twoHyphens + boundary + crlf);
            dos.writeBytes("Content-Type: " + mimeType + crlf);
            dos.writeBytes("Content-Disposition: form-data; name=\"" +
                    attachmentName + "\";filename=\"" +
                    attachmentFileName + "\"" + crlf);
            dos.writeBytes(crlf);
            dos.write(data);
            dos.writeBytes(crlf);
            dos.flush();
        }

        static void endMultipart(OutputStream os) throws IOException{
            DataOutputStream dos = new DataOutputStream(os);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + crlf);
            dos.flush();
        }
        static void postData(OutputStream os, String data) throws IOException {
            OutputStreamWriter osw = new OutputStreamWriter(os, CHARSET);
            osw.write(data);
            osw.flush();
            osw.close();
        }

        static String readResponse(InputStream is) throws IOException {
            InputStreamReader isr = new InputStreamReader(is, CHARSET);
            BufferedReader br = new BufferedReader(isr);

            StringBuilder sb = new StringBuilder();
            String line;
            while((line = br.readLine()) != null){
                sb.append(line);
            }

            String returnString = sb.toString();

            br.close();
            isr.close();

            return returnString;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(APITaskResult<T> apiTaskResult) {
            if(apiTaskResult.success)
                responder.success(apiTaskResult.result);
            else
                responder.error(apiTaskResult.errorMessage);
            super.onPostExecute(apiTaskResult);
        }

        @Override
        protected void onCancelled() {
            responder.cancelled();
        }
    }

    public static final class UploadImageAsyncTask extends APIAsyncTask<Void>{
        private String accessToken;
        private Bitmap image;
        private int listingId;

        UploadImageAsyncTask(String accessToken, int listingId, Bitmap image, APIResponder<Void> responder) {
            super(responder);
            this.accessToken = accessToken;
            this.image = image;
            this.listingId = listingId;
        }

        @Override
        protected APITaskResult<Void> doInBackground(Void... params) {
            APITaskResult<Void> returnResult = new APITaskResult<Void>();

            String urlString = String.format("%s%s", API_ENDPOINT, IMAGEUPLOAD_SUFFIX);

            try {
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                setBearer(accessToken, conn);
                configureMultipart(conn);

                OutputStream os = conn.getOutputStream();
                attachMultipartString(os, "id", String.format(Locale.CANADA, "%d", listingId));

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 72, baos);

                uploadAttachment(os, baos.toByteArray(), "image", "image.jpg", "image/jpeg");
                endMultipart(os);
                baos.close();
                os.close();

                int responseCode = conn.getResponseCode();

                if(responseCode != HttpURLConnection.HTTP_OK)
                    throw new Exception("Invalid request");

                returnResult.success = true;

            } catch (MalformedURLException e) {
                returnResult.errorMessage = "The URL was malformed.";
            } catch (IOException e) {
                returnResult.errorMessage = "Unable to make request.";
            } catch(Exception e){
                returnResult.errorMessage = e.getMessage();
            }
            finally {
                return returnResult;
            }
        }
    }

    public static final class GetListingsAsyncTask extends APIAsyncTask<Listing[]>{
        private String accessToken;

        GetListingsAsyncTask(String accessToken, APIResponder<Listing[]> responder) {
            super(responder);
            this.accessToken = accessToken;
        }

        @Override
        protected APITaskResult<Listing[]> doInBackground(Void... params) {
            APITaskResult<Listing[]> returnResult = new APITaskResult<Listing[]>();

            String urlString = String.format("%s%s?per-page=1000&sort=-datePosted", API_ENDPOINT, LISTINGS_SUFFIX);

            try {
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                setBearer(accessToken, conn);

                int responseCode = conn.getResponseCode();

                if(responseCode != HttpURLConnection.HTTP_OK)
                    throw new Exception("Invalid request");


                InputStream is = conn.getInputStream();
                String data = readResponse(is);
                is.close();

                JSONObject response = new JSONObject(data);

                Listing[] arrListing = Listing.fromJsonArray(response.getJSONArray("listings"));

                returnResult.result = arrListing;
                returnResult.success = true;

            } catch (MalformedURLException e) {
                returnResult.errorMessage = "The URL was malformed.";
            } catch (IOException e) {
                returnResult.errorMessage = "Unable to make request.";
            } catch(Exception e){
                returnResult.errorMessage = e.getMessage();
            }
            finally {
                return returnResult;
            }
        }
    }

    public static final class GetScavengerListingsAsyncTask extends APIAsyncTask<Listing[]>{
        private String accessToken;

        GetScavengerListingsAsyncTask(String accessToken, APIResponder<Listing[]> responder) {
            super(responder);
            this.accessToken = accessToken;
        }

        @Override
        protected APITaskResult<Listing[]> doInBackground(Void... params) {
            APITaskResult<Listing[]> returnResult = new APITaskResult<Listing[]>();

            String urlString = String.format("%s%s?per-page=1000&sort=-datePosted", API_ENDPOINT, SCAVENGERLISTINGS_SUFFIX);

            try {
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                setBearer(accessToken, conn);

                int responseCode = conn.getResponseCode();

                if(responseCode != HttpURLConnection.HTTP_OK)
                    throw new Exception("Invalid request");


                InputStream is = conn.getInputStream();
                String data = readResponse(is);
                is.close();

                JSONObject response = new JSONObject(data);

                Listing[] arrListing = Listing.fromJsonArray(response.getJSONArray("listings"));

                returnResult.result = arrListing;
                returnResult.success = true;

            } catch (MalformedURLException e) {
                returnResult.errorMessage = "The URL was malformed.";
            } catch (IOException e) {
                returnResult.errorMessage = "Unable to make request.";
            } catch(Exception e){
                returnResult.errorMessage = e.getMessage();
            }
            finally {
                return returnResult;
            }
        }
    }

    public static final class GetMyListingsAsyncTask extends APIAsyncTask<Listing[]>{
        private String accessToken;

        GetMyListingsAsyncTask(String accessToken, APIResponder<Listing[]> responder) {
            super(responder);
            this.accessToken = accessToken;
        }

        @Override
        protected APITaskResult<Listing[]> doInBackground(Void... params) {
            APITaskResult<Listing[]> returnResult = new APITaskResult<Listing[]>();

            String urlString = String.format("%s%s?per-page=1000", API_ENDPOINT, MYLISTINGS_SUFFIX);

            try {
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                setBearer(accessToken, conn);

                int responseCode = conn.getResponseCode();

                if(responseCode != HttpURLConnection.HTTP_OK)
                    throw new Exception("Invalid request");


                InputStream is = conn.getInputStream();
                String data = readResponse(is);
                is.close();

                JSONObject response = new JSONObject(data);

                Listing[] arrListing = Listing.fromJsonArray(response.getJSONArray("listings"));

                returnResult.result = arrListing;
                returnResult.success = true;

            } catch (MalformedURLException e) {
                returnResult.errorMessage = "The URL was malformed.";
            } catch (IOException e) {
                returnResult.errorMessage = "Unable to make request.";
            } catch(Exception e){
                returnResult.errorMessage = e.getMessage();
            }
            finally {
                return returnResult;
            }
        }
    }

    public static final class GetMyBeaconsAsyncTask extends APIAsyncTask<Beacon[]>{
        private String accessToken;

        GetMyBeaconsAsyncTask(String accessToken, APIResponder<Beacon[]> responder) {
            super(responder);
            this.accessToken = accessToken;
        }

        @Override
        protected APITaskResult<Beacon[]> doInBackground(Void... params) {
            APITaskResult<Beacon[]> returnResult = new APITaskResult<Beacon[]>();

            String urlString = String.format("%s%s?per-page=1000", API_ENDPOINT, MYBEACONS_SUFFIX);

            try {
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                setBearer(accessToken, conn);

                int responseCode = conn.getResponseCode();

                if(responseCode != HttpURLConnection.HTTP_OK)
                    throw new Exception("Invalid request");


                InputStream is = conn.getInputStream();
                String data = readResponse(is);
                is.close();

                JSONObject response = new JSONObject(data);

                Beacon[] arrBeacons = Beacon.fromJsonArray(response.getJSONArray("beacons"));

                returnResult.result = arrBeacons;
                returnResult.success = true;

            } catch (MalformedURLException e) {
                returnResult.errorMessage = "The URL was malformed.";
            } catch (IOException e) {
                returnResult.errorMessage = "Unable to make request.";
            } catch(Exception e){
                returnResult.errorMessage = e.getMessage();
            }
            finally {
                return returnResult;
            }
        }
    }

    public static final class VinDecodeAsyncTask extends APIAsyncTask<VinDecodeResult>{
        private String accessToken;
        private String vinNumber;
        VinDecodeAsyncTask(String accessToken, String vinNumber, APIResponder<VinDecodeResult> responder) {
            super(responder);
            this.accessToken = accessToken;
            this.vinNumber = vinNumber;
        }

        @Override
        protected APITaskResult<VinDecodeResult> doInBackground(Void... params) {
            APITaskResult<VinDecodeResult> returnResult = new APITaskResult<VinDecodeResult>();

            String urlString = String.format("%s%s?vin=%s", API_ENDPOINT, VINDECODE_SUFFIX, vinNumber);

            try {
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                setBearer(accessToken, conn);

                int responseCode = conn.getResponseCode();

                if(responseCode != HttpURLConnection.HTTP_OK)
                    throw new Exception("Invalid request");


                InputStream is = conn.getInputStream();
                String data = readResponse(is);
                is.close();

                JSONObject response = new JSONObject(data);

                if (response.has("message"))
                    throw new Exception(response.getString("message"));

                returnResult.result = VinDecodeResult.fromJsonObject(response.getJSONObject("listing"));
                returnResult.success = true;

            } catch (MalformedURLException e) {
                returnResult.errorMessage = "The URL was malformed.";
            } catch (IOException e) {
                returnResult.errorMessage = "Unable to make request.";
            } catch(Exception e){
                returnResult.errorMessage = e.getMessage();
            }
            finally {
                return returnResult;
            }
        }
    }

    public static final class CreateListingAsyncTask extends APIAsyncTask<Listing>{
        Listing listing;
        String accessToken;

        CreateListingAsyncTask(String accessToken, Listing listing, APIResponder<Listing> responder) {
            super(responder);
            this.listing = listing;
            this.accessToken = accessToken;
        }

        @Override
        protected APITaskResult<Listing> doInBackground(Void... params) {
            APITaskResult<Listing> returnResult = new APITaskResult<Listing>();

            String urlString = String.format("%s%s", API_ENDPOINT, LISTINGS_SUFFIX);

            try {
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                setBearer(accessToken, conn);
                configurePost(conn);

                JSONObject postData = new JSONObject();
                postData.put("listing", listing.toJsonObject());

                OutputStream os = conn.getOutputStream();
                postData(os, postData.toString());
                os.close();

                int responseCode = conn.getResponseCode();

                if(responseCode != HttpURLConnection.HTTP_OK)
                    throw new Exception("Invalid request");


                InputStream is = conn.getInputStream();
                String data = readResponse(is);
                is.close();

                JSONObject response = new JSONObject(data);

                if(response.has("errors") || response.has("error"))
                    throw new Exception(response.has("message") ? response.getString("message") : "An unknown error occurred");

                Listing result = Listing.fromJsonObject(response.getJSONObject("listing"));

                returnResult.result = result;
                returnResult.success = true;

            } catch (MalformedURLException e) {
                returnResult.errorMessage = "The URL was malformed.";
            } catch (IOException e) {
                returnResult.errorMessage = "Unable to make request.";
            } catch(Exception e){
                returnResult.errorMessage = e.getMessage();
            }
            finally {
                return returnResult;
            }
        }
    }

    public static final class UpdateListingAsyncTask extends APIAsyncTask<Listing>{
        int listingId;
        Listing listing;
        String accessToken;

        UpdateListingAsyncTask(String accessToken, int listingId, Listing listing, APIResponder<Listing> responder) {
            super(responder);
            this.listingId = listingId;
            this.listing = listing;
            this.accessToken = accessToken;
        }

        @Override
        protected APITaskResult<Listing> doInBackground(Void... params) {
            APITaskResult<Listing> returnResult = new APITaskResult<Listing>();

            String urlString = String.format(Locale.CANADA, "%s%s/%d/%s", API_ENDPOINT, LISTINGS_SUFFIX, listingId, LISTINGUPDATE_SUFFIX);

            try {
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                setBearer(accessToken, conn);
                configurePost(conn);

                JSONObject postData = new JSONObject();
                postData.put("listing", listing.toJsonObject());

                OutputStream os = conn.getOutputStream();
                postData(os, postData.toString());
                os.close();

                int responseCode = conn.getResponseCode();

                if(responseCode != HttpURLConnection.HTTP_OK)
                    throw new Exception("Invalid request");


                InputStream is = conn.getInputStream();
                String data = readResponse(is);
                is.close();

                JSONObject response = new JSONObject(data);

                if(response.has("errors") || response.has("error"))
                    throw new Exception(response.has("message") ? response.getString("message") : "An unknown error occurred");

                Listing result = Listing.fromJsonObject(response.getJSONObject("listing"));

                returnResult.result = result;
                returnResult.success = true;

            } catch (MalformedURLException e) {
                returnResult.errorMessage = "The URL was malformed.";
            } catch (IOException e) {
                returnResult.errorMessage = "Unable to make request.";
            } catch(Exception e){
                returnResult.errorMessage = e.getMessage();
            }
            finally {
                return returnResult;
            }
        }
    }

    public static final class DeleteListingAsyncTask extends APIAsyncTask<Void>{
        Listing listing;
        String accessToken;

        DeleteListingAsyncTask(String accessToken, Listing listing, APIResponder<Void> responder) {
            super(responder);
            this.listing = listing;
            this.accessToken = accessToken;
        }

        @Override
        protected APITaskResult<Void> doInBackground(Void... params) {
            APITaskResult<Void> returnResult = new APITaskResult<Void>();

            String urlString = String.format(Locale.CANADA, "%s%s/%d/%s", API_ENDPOINT, LISTINGS_SUFFIX, listing.getId(), LISTINGSDELETE_SUFFIX);

            try {
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                setBearer(accessToken, conn);

                int responseCode = conn.getResponseCode();

                if(responseCode != HttpURLConnection.HTTP_OK)
                    throw new Exception("Invalid request");


                InputStream is = conn.getInputStream();
                String data = readResponse(is);
                is.close();

                JSONObject response = new JSONObject(data);

                if(response.has("error"))
                    throw new Exception(response.has("message") ? response.getString("message") : response.getString("error"));

                returnResult.success = true;

            } catch (MalformedURLException e) {
                returnResult.errorMessage = "The URL was malformed.";
            } catch (IOException e) {
                returnResult.errorMessage = "Unable to make request.";
            } catch(Exception e){
                returnResult.errorMessage = e.getMessage();
            }
            finally {
                return returnResult;
            }
        }
    }

    public static final class GetListingFromBeaconAsyncTask extends APIAsyncTask<Listing>{
        String uuid;
        int major;
        int minor;
        String accessToken;

        GetListingFromBeaconAsyncTask(String accessToken, String uuid, int major, int minor, APIResponder<Listing> responder) {
            super(responder);
            this.uuid = uuid;
            this.major = major;
            this.minor = minor;
            this.accessToken = accessToken;
        }

        @Override
        protected APITaskResult<Listing> doInBackground(Void... params) {
            APITaskResult<Listing> returnResult = new APITaskResult<Listing>();

            String urlString = String.format(Locale.CANADA, "%s%s/%s/%d/%d", API_ENDPOINT, FROMBEACON_SUFFIX, uuid, major, minor);

            try {
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                setBearer(accessToken, conn);

                int responseCode = conn.getResponseCode();

                if(responseCode != HttpURLConnection.HTTP_OK)
                    throw new Exception("Invalid request");


                InputStream is = conn.getInputStream();
                String data = readResponse(is);
                is.close();

                JSONObject response = new JSONObject(data);

                if(response.has("errors") || response.has("error"))
                    throw new Exception(response.has("message") ? response.getString("message") : "An unknown error occurred");

                Listing result = Listing.fromJsonObject(response.getJSONObject("listing"));

                returnResult.result = result;
                returnResult.success = true;

            } catch (MalformedURLException e) {
                returnResult.errorMessage = "The URL was malformed.";
            } catch (IOException e) {
                returnResult.errorMessage = "Unable to make request.";
            } catch(Exception e){
                returnResult.errorMessage = e.getMessage();
            }
            finally {
                return returnResult;
            }
        }
    }

    public static final class CreateScavengerListingAsyncTask extends APIAsyncTask<Listing>{
        Listing listing;
        String accessToken;

        CreateScavengerListingAsyncTask(String accessToken, Listing listing, APIResponder<Listing> responder) {
            super(responder);
            this.listing = listing;
            this.accessToken = accessToken;
        }

        @Override
        protected APITaskResult<Listing> doInBackground(Void... params) {
            APITaskResult<Listing> returnResult = new APITaskResult<Listing>();

            String urlString = String.format("%s%s", API_ENDPOINT, SCAVENGERLISTINGS_SUFFIX);

            try {
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                setBearer(accessToken, conn);
                configurePost(conn);

                JSONObject postData = new JSONObject();
                postData.put("listing", listing.toJsonObject());

                OutputStream os = conn.getOutputStream();
                postData(os, postData.toString());
                os.close();

                int responseCode = conn.getResponseCode();

                if(responseCode != HttpURLConnection.HTTP_OK)
                    throw new Exception("Invalid request");


                InputStream is = conn.getInputStream();
                String data = readResponse(is);
                is.close();

                JSONObject response = new JSONObject(data);

                if(response.has("errors") || response.has("error"))
                    throw new Exception(response.has("message") ? response.getString("message") : "An unknown error occurred");

                Listing result = Listing.fromJsonObject(response.getJSONObject("listing"));

                returnResult.result = result;
                returnResult.success = true;

            } catch (MalformedURLException e) {
                returnResult.errorMessage = "The URL was malformed.";
            } catch (IOException e) {
                returnResult.errorMessage = "Unable to make request.";
            } catch(Exception e){
                returnResult.errorMessage = e.getMessage();
            }
            finally {
                return returnResult;
            }
        }
    }

    public static final class LoginAsyncTask extends APIAsyncTask<UserInformation>{
        private String email;
        private String password;

        LoginAsyncTask(String email, String password, APIResponder<UserInformation> responder) {
            super(responder);
            this.email = email;
            this.password = password;
        }

        @Override
        protected APITaskResult<UserInformation> doInBackground(Void... params) {
            APITaskResult<UserInformation> returnResult = new APITaskResult<UserInformation>();

            String urlString = String.format("%s%s", API_ENDPOINT, LOGIN_SUFFIX);

            try {
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                configurePost(conn);


                JSONObject credentials = new JSONObject();
                credentials.put("email", this.email);
                credentials.put("password", this.password);


                OutputStream os = conn.getOutputStream();
                postData(os, credentials.toString());
                os.close();

                int responseCode = conn.getResponseCode();

                if(responseCode != HttpURLConnection.HTTP_OK)
                    throw new Exception("Invalid request");


                InputStream is = conn.getInputStream();
                String data = readResponse(is);
                is.close();

                JSONObject response = new JSONObject(data);

                if(response.getString("result").equals("error"))
                    throw new Exception(response.getString("message"));

                UserInformation ui = UserInformation.fromJsonObject(response);

                returnResult.result = ui;
                returnResult.success = true;

            } catch (MalformedURLException e) {
                returnResult.errorMessage = "The URL was malformed.";
            } catch (IOException e) {
                returnResult.errorMessage = "Unable to make request.";
            } catch(Exception e){
                returnResult.errorMessage = e.getMessage();
            }
            finally {
                return returnResult;
            }
        }
    }

    public static final class ValidateTokenAsyncTask extends APIAsyncTask<UserInformation>{
        private String accessToken;

        ValidateTokenAsyncTask(String accessToken, APIResponder<UserInformation> responder) {
            super(responder);
            this.accessToken = accessToken;
        }

        @Override
        protected APITaskResult<UserInformation> doInBackground(Void... params) {
            APITaskResult<UserInformation> returnResult = new APITaskResult<UserInformation>();

            String urlString = String.format("%s%s/%s", API_ENDPOINT, VALIDATE_SUFFIX, accessToken);

            try {
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();

                int responseCode = conn.getResponseCode();

                if(responseCode != HttpURLConnection.HTTP_OK)
                    throw new Exception("Invalid request");


                InputStream is = conn.getInputStream();
                String data = readResponse(is);
                is.close();

                JSONObject response = new JSONObject(data);

                if(response.getString("result").equals("error"))
                    throw new Exception(response.getString("message"));

                UserInformation ui = UserInformation.fromJsonObject(response);

                returnResult.result = ui;
                returnResult.success = true;

            } catch (MalformedURLException e) {
                returnResult.errorMessage = "The URL was malformed.";
            } catch (IOException e) {
                returnResult.errorMessage = "Unable to make request.";
            } catch(Exception e){
                returnResult.errorMessage = e.getMessage();
            }
            finally {
                return returnResult;
            }
        }
    }

    public static final class DownloadImageAsyncTask extends APIAsyncTask<Bitmap>{
        private String imageUrl;

        DownloadImageAsyncTask(String imageUrl, APIResponder<Bitmap> responder) {
            super(responder);
            this.imageUrl = imageUrl;
        }

        @Override
        protected APITaskResult<Bitmap> doInBackground(Void... params) {
            APITaskResult<Bitmap> returnResult = new APITaskResult<Bitmap>();
            returnResult.result = ImageCacheManager.getImage(imageUrl);

            if(returnResult.result != null){
                returnResult.success = true;
                return returnResult;
            }

            try {
                URL url = new URL(imageUrl);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();


                int responseCode = conn.getResponseCode();

                if(responseCode != HttpURLConnection.HTTP_OK)
                    throw new Exception("Invalid request");


                InputStream is = conn.getInputStream();
                returnResult.result = BitmapFactory.decodeStream(is);
                ImageCacheManager.storeImage(imageUrl, returnResult.result);
                is.close();

                returnResult.success = true;

            } catch (MalformedURLException e) {
                returnResult.errorMessage = "The URL was malformed.";
            } catch (IOException e) {
                returnResult.errorMessage = "Unable to make request.";
            } catch(Exception e){
                returnResult.errorMessage = e.getMessage();
            }
            finally {
                return returnResult;
            }
        }
    }

    public static final class RegisterAsyncTask extends APIAsyncTask<UserInformation>{
        private String firstName;
        private String lastName;
        private String email;
        private String password;

        RegisterAsyncTask(String firstName, String lastName, String email, String password, APIResponder<UserInformation> responder) {
            super(responder);
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
            this.password = password;
        }

        @Override
        protected APITaskResult<UserInformation> doInBackground(Void... params) {
            APITaskResult<UserInformation> returnResult = new APITaskResult<UserInformation>();

            String urlString = String.format("%s%s", API_ENDPOINT, REGISTER_SUFFIX);

            try {
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                configurePost(conn);


                JSONObject credentials = new JSONObject();
                credentials.put("firstName", this.firstName);
                credentials.put("lastName", this.lastName);
                credentials.put("email", this.email);
                credentials.put("password", this.password);


                OutputStream os = conn.getOutputStream();
                postData(os, credentials.toString());
                os.close();

                int responseCode = conn.getResponseCode();

                if(responseCode != HttpURLConnection.HTTP_OK)
                    throw new Exception("Invalid request");


                InputStream is = conn.getInputStream();
                String data = readResponse(is);
                is.close();

                JSONObject response = new JSONObject(data);

                if(response.getString("result").equals("error"))
                    throw new Exception(response.getString("message"));

                UserInformation ui = UserInformation.fromJsonObject(response);

                returnResult.result = ui;
                returnResult.success = true;

            } catch (MalformedURLException e) {
                returnResult.errorMessage = "The URL was malformed.";
            } catch (IOException e) {
                returnResult.errorMessage = "Unable to make request.";
            } catch(Exception e){
                returnResult.errorMessage = e.getMessage();
            }
            finally {
                return returnResult;
            }
        }
    }

    public static final class UpdateUserAsyncTask extends APIAsyncTask<UserInformation>{
        private UserInformation userInfo;
        UpdateUserAsyncTask(UserInformation userInfo, APIResponder<UserInformation> responder) {
            super(responder);
            this.userInfo = userInfo;
        }

        @Override
        protected APITaskResult<UserInformation> doInBackground(Void... params) {
            APITaskResult<UserInformation> returnResult = new APITaskResult<UserInformation>();

            String urlString = String.format("%s%s", API_ENDPOINT, UPDATEUSER_SUFFIX);

            try {
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                configurePost(conn);


                JSONObject credentials = userInfo.toJsonObject();


                OutputStream os = conn.getOutputStream();
                postData(os, credentials.toString());
                os.close();

                int responseCode = conn.getResponseCode();

                if(responseCode != HttpURLConnection.HTTP_OK)
                    throw new Exception("Invalid request");


                InputStream is = conn.getInputStream();
                String data = readResponse(is);
                is.close();

                JSONObject response = new JSONObject(data);

                if(response.getString("result").equals("error"))
                    throw new Exception(response.getString("message"));

                UserInformation ui = UserInformation.fromJsonObject(response);

                returnResult.result = ui;
                returnResult.success = true;

            } catch (MalformedURLException e) {
                returnResult.errorMessage = "The URL was malformed.";
            } catch (IOException e) {
                returnResult.errorMessage = "Unable to make request.";
            } catch(Exception e){
                returnResult.errorMessage = e.getMessage();
            }
            finally {
                return returnResult;
            }
        }
    }

    public static final class GetVideosAsyncTask extends APIAsyncTask<VideoEntry[]>{
        GetVideosAsyncTask(APIResponder<VideoEntry[]> responder) {
            super(responder);
        }

        @Override
        protected APITaskResult<VideoEntry[]> doInBackground(Void... params) {
            APITaskResult<VideoEntry[]> returnResult = new APITaskResult<VideoEntry[]>();

            String urlString = String.format("%s%s", API_ENDPOINT, VIDEOS_SUFFIX);

            try {
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();

                int responseCode = conn.getResponseCode();

                if(responseCode != HttpURLConnection.HTTP_OK)
                    throw new Exception("Invalid request");


                InputStream is = conn.getInputStream();
                String data = readResponse(is);
                is.close();

                JSONObject response = new JSONObject(data);

                VideoEntry[] arrListing = VideoEntry.fromJsonArray(response.getJSONArray("videos"));

                returnResult.result = arrListing;
                returnResult.success = true;

            } catch (MalformedURLException e) {
                returnResult.errorMessage = "The URL was malformed.";
            } catch (IOException e) {
                returnResult.errorMessage = "Unable to make request.";
            } catch(Exception e){
                returnResult.errorMessage = e.getMessage();
            }
            finally {
                return returnResult;
            }
        }
    }

    public static final class GetLatLonAsyncTask extends APIAsyncTask<double[]>{
        private String address;
        GetLatLonAsyncTask(String address, APIResponder<double[]> responder) {
            super(responder);
            this.address = address;
        }

        @Override
        protected APITaskResult<double[]> doInBackground(Void... params) {
            APITaskResult<double[]> returnResult = new APITaskResult<double[]>();

            String urlString = "";
            try {
                urlString = String.format(GEOCODE_URLFORMAT, URLEncoder.encode(address, CHARSET));

                if(geoMap.containsKey(urlString))
                    return geoMap.get(urlString);

                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();

                int responseCode = conn.getResponseCode();

                if(responseCode != HttpURLConnection.HTTP_OK)
                    throw new Exception("Invalid request");


                InputStream is = conn.getInputStream();
                String data = readResponse(is);
                is.close();

                JSONObject response = new JSONObject(data);

                double lat = response.getJSONArray("results").getJSONObject(0)
                        .getJSONObject("geometry").getJSONObject("location")
                        .getDouble("lat");

                double lon = response.getJSONArray("results").getJSONObject(0)
                        .getJSONObject("geometry").getJSONObject("location")
                        .getDouble("lng");

                returnResult.result = new double[] {lat, lon};
                returnResult.success = true;

            } catch (MalformedURLException e) {
                returnResult.errorMessage = "The URL was malformed.";
            } catch (IOException e) {
                returnResult.errorMessage = "Unable to make request.";
            } catch(Exception e){
                returnResult.errorMessage = e.getMessage();
            }
            finally {
                geoMap.put(urlString, returnResult);
            }

            return returnResult;
        }
    }

    public static final class HoldListingAsyncTask extends APIAsyncTask<Void>{
        private String accessToken;
        private int listingId;

        HoldListingAsyncTask(String accessToken, int listingId, APIResponder<Void> responder) {
            super(responder);
            this.accessToken = accessToken;
            this.listingId = listingId;
        }

        @Override
        protected APITaskResult<Void> doInBackground(Void... params) {
            APITaskResult<Void> returnResult = new APITaskResult<Void>();

            String urlString = String.format(Locale.CANADA, "%s%s/%d/%s", API_ENDPOINT, LISTINGS_SUFFIX, listingId, HOLD_SUFFIX);

            try {
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                setBearer(accessToken, conn);

                int responseCode = conn.getResponseCode();

                if(responseCode != HttpURLConnection.HTTP_OK)
                    throw new Exception("Invalid request");


                InputStream is = conn.getInputStream();
                String data = readResponse(is);
                is.close();

                JSONObject response = new JSONObject(data);

                if(response.getString("result").equals("error"))
                    throw new Exception(response.getString("message"));

                returnResult.success = true;

            } catch (MalformedURLException e) {
                returnResult.errorMessage = "The URL was malformed.";
            } catch (IOException e) {
                returnResult.errorMessage = "Unable to make request.";
            } catch(Exception e){
                returnResult.errorMessage = e.getMessage();
            }
            finally {
                return returnResult;
            }
        }
    }

    public static final class PurchaseListingAsyncTask extends APIAsyncTask<Void>{
        private String accessToken;
        private int listingId;

        PurchaseListingAsyncTask(String accessToken, int listingId, APIResponder<Void> responder) {
            super(responder);
            this.accessToken = accessToken;
            this.listingId = listingId;
        }

        @Override
        protected APITaskResult<Void> doInBackground(Void... params) {
            APITaskResult<Void> returnResult = new APITaskResult<Void>();

            String urlString = String.format(Locale.CANADA, "%s%s/%d/%s", API_ENDPOINT, LISTINGS_SUFFIX, listingId, PURCHASE_SUFFIX);

            try {
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                setBearer(accessToken, conn);

                int responseCode = conn.getResponseCode();

                if(responseCode != HttpURLConnection.HTTP_OK)
                    throw new Exception("Invalid request");


                InputStream is = conn.getInputStream();
                String data = readResponse(is);
                is.close();

                JSONObject response = new JSONObject(data);

                if(response.getString("result").equals("error"))
                    throw new Exception(response.getString("message"));

                returnResult.success = true;

            } catch (MalformedURLException e) {
                returnResult.errorMessage = "The URL was malformed.";
            } catch (IOException e) {
                returnResult.errorMessage = "Unable to make request.";
            } catch(Exception e){
                returnResult.errorMessage = e.getMessage();
            }
            finally {
                return returnResult;
            }
        }
    }

    public static final class OfferOnListingAsyncTask extends APIAsyncTask<Void>{
        private String accessToken;
        private int listingId;
        private double offerPrice;

        OfferOnListingAsyncTask(String accessToken, int listingId, double offerPrice, APIResponder<Void> responder) {
            super(responder);
            this.accessToken = accessToken;
            this.listingId = listingId;
            this.offerPrice = offerPrice;
        }

        @Override
        protected APITaskResult<Void> doInBackground(Void... params) {
            APITaskResult<Void> returnResult = new APITaskResult<Void>();

            String urlString = String.format(Locale.CANADA, "%s%s/%d/%s", API_ENDPOINT, LISTINGS_SUFFIX, listingId, OFFER_SUFFIX);

            try {
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                setBearer(accessToken, conn);
                configurePost(conn);


                JSONObject postData = new JSONObject();
                postData.put("offer", offerPrice);

                OutputStream os = conn.getOutputStream();
                postData(os, postData.toString());
                os.close();


                int responseCode = conn.getResponseCode();

                if(responseCode != HttpURLConnection.HTTP_OK)
                    throw new Exception("Invalid request");

                InputStream is = conn.getInputStream();
                String data = readResponse(is);
                is.close();

                JSONObject response = new JSONObject(data);

                if(response.getString("result").equals("error"))
                    throw new Exception(response.getString("message"));

                returnResult.success = true;

            } catch (MalformedURLException e) {
                returnResult.errorMessage = "The URL was malformed.";
            } catch (IOException e) {
                returnResult.errorMessage = "Unable to make request.";
            } catch(Exception e){
                returnResult.errorMessage = e.getMessage();
            }
            finally {
                return returnResult;
            }
        }
    }

    public static final class RenameBeaconAsyncTask extends APIAsyncTask<Beacon>{
        private String accessToken;
        private Beacon beacon;
        private String name;

        RenameBeaconAsyncTask(String accessToken, Beacon beacon, String name, APIResponder<Beacon> responder) {
            super(responder);
            this.accessToken = accessToken;
            this.beacon = beacon;
            this.name = name;
        }

        @Override
        protected APITaskResult<Beacon> doInBackground(Void... params) {
            APITaskResult<Beacon> returnResult = new APITaskResult<>();

            String urlString = String.format(Locale.CANADA, "%s%s/%s/%d/%d/%s", API_ENDPOINT, BEACONS_SUFFIX, beacon.getUuid(), beacon.getMajor(), beacon.getMinor(), RENAME_SUFFIX);

            try {
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                setBearer(accessToken, conn);
                configurePost(conn);


                JSONObject postData = new JSONObject();
                postData.put("name", name);

                OutputStream os = conn.getOutputStream();
                postData(os, postData.toString());
                os.close();


                int responseCode = conn.getResponseCode();

                if(responseCode != HttpURLConnection.HTTP_OK)
                    throw new Exception("Invalid request");

                InputStream is = conn.getInputStream();
                String data = readResponse(is);
                is.close();

                JSONObject response = new JSONObject(data);

                if(response.getString("result").equals("error"))
                    throw new Exception(response.getString("message"));

                returnResult.result = Beacon.fromJsonObject(response.getJSONObject("beacon"));
                returnResult.success = true;

            } catch (MalformedURLException e) {
                returnResult.errorMessage = "The URL was malformed.";
            } catch (IOException e) {
                returnResult.errorMessage = "Unable to make request.";
            } catch(Exception e){
                returnResult.errorMessage = e.getMessage();
            }
            finally {
                return returnResult;
            }
        }
    }

    public static final class AssociateBeaconAsyncTask extends APIAsyncTask<Beacon>{
        private String accessToken;
        private Beacon beacon;
        private Listing listing;

        AssociateBeaconAsyncTask(String accessToken, Beacon beacon, Listing listing, APIResponder<Beacon> responder) {
            super(responder);
            this.accessToken = accessToken;
            this.beacon = beacon;
            this.listing = listing;
        }

        @Override
        protected APITaskResult<Beacon> doInBackground(Void... params) {
            APITaskResult<Beacon> returnResult = new APITaskResult<>();

            String urlString = String.format(Locale.CANADA, "%s%s/%s/%d/%d/%s", API_ENDPOINT, BEACONS_SUFFIX, beacon.getUuid(), beacon.getMajor(), beacon.getMinor(), ASSOCIATE_SUFFIX);

            try {
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                setBearer(accessToken, conn);
                configurePost(conn);


                JSONObject postData = new JSONObject();
                postData.put("listing_id", listing == null ? JSONObject.NULL : listing.getId());

                OutputStream os = conn.getOutputStream();
                postData(os, postData.toString());
                os.close();


                int responseCode = conn.getResponseCode();

                if(responseCode != HttpURLConnection.HTTP_OK)
                    throw new Exception("Invalid request");

                InputStream is = conn.getInputStream();
                String data = readResponse(is);
                is.close();

                JSONObject response = new JSONObject(data);

                if(response.getString("result").equals("error"))
                    throw new Exception(response.getString("message"));

                returnResult.result = Beacon.fromJsonObject(response.getJSONObject("beacon"));
                returnResult.success = true;

            } catch (MalformedURLException e) {
                returnResult.errorMessage = "The URL was malformed.";
            } catch (IOException e) {
                returnResult.errorMessage = "Unable to make request.";
            } catch(Exception e){
                returnResult.errorMessage = e.getMessage();
            }
            finally {
                return returnResult;
            }
        }
    }

    public static final class AddBeaconAsyncTask extends APIAsyncTask<Beacon>{
        private String accessToken;
        private String code;

        AddBeaconAsyncTask(String accessToken, String code, APIResponder<Beacon> responder) {
            super(responder);
            this.accessToken = accessToken;
            this.code = code;
        }

        @Override
        protected APITaskResult<Beacon> doInBackground(Void... params) {
            APITaskResult<Beacon> returnResult = new APITaskResult<>();

            String urlString = String.format(Locale.CANADA, "%s%s", API_ENDPOINT, ADDBEACON_SUFFIX);

            try {
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                setBearer(accessToken, conn);
                configurePost(conn);


                JSONObject postData = new JSONObject();
                postData.put("code", code);

                OutputStream os = conn.getOutputStream();
                postData(os, postData.toString());
                os.close();


                int responseCode = conn.getResponseCode();

                if(responseCode != HttpURLConnection.HTTP_OK)
                    throw new Exception("Invalid request");

                InputStream is = conn.getInputStream();
                String data = readResponse(is);
                is.close();

                JSONObject response = new JSONObject(data);

                if(response.getString("result").equals("error"))
                    throw new Exception(response.getString("message"));

                returnResult.result = Beacon.fromJsonObject(response.getJSONObject("beacon"));
                returnResult.success = true;

            } catch (MalformedURLException e) {
                returnResult.errorMessage = "The URL was malformed.";
            } catch (IOException e) {
                returnResult.errorMessage = "Unable to make request.";
            } catch(Exception e){
                returnResult.errorMessage = e.getMessage();
            }
            finally {
                return returnResult;
            }
        }
    }
}
