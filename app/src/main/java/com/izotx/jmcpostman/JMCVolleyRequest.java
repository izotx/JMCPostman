package com.izotx.jmcpostman;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
//import org.apache.http.entity.mime.MultipartEntityBuilder;

/**
 * Created by janusz on 10/3/15.
 */



public class JMCVolleyRequest {

    String mimeType;
    DataOutputStream dos = null;
    String lineEnd = "\r\n";
    String boundary = "apiclient-" + System.currentTimeMillis();
    String twoHyphens = "--";
    int bytesRead, bytesAvailable, bufferSize;
    byte[] buffer;
    int maxBufferSize = 1024 * 1024;

    String TAG = "VOLLEY";

    public enum APIKeys{
        kAddMethodName("add"),
        kTokenFieldName("tok"),
        kImageTypeName("content_type"),
        kImageFieldName("image"),
        kEndpointURL("http://izotx.com/api/");

        private final String text;

        /**
         * @param text
         */
        private APIKeys(final String text) {
            this.text = text;
        }

        /* (non-Javadoc)
         * @see java.lang.Enum#toString()
         */
        @Override
        public String toString() {
            return text;
        }
    }


    private void buildMultipart(){
      //  MultipartEntityBuilder builder = MultipartEntityBuilder.create();


    }



    public void sendFile(Bitmap bitmap, String token, String contentType, Context context) {
        //Prepare request
        final Context mContext = context;
        final String mToken = token;
        final String mContentType = contentType;
        final Bitmap mBitmap = bitmap;
        mimeType = "multipart/form-data; boundary="+boundary;

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        final byte[] bitmapData = byteArrayOutputStream.toByteArray();


        String url = APIKeys.kEndpointURL.toString();

        BaseVolleyRequest baseVolleyRequest = new BaseVolleyRequest(Request.Method.POST, url, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String s = "";
                try{
                    s = new String(response.data, "UTF-8");
                }
                catch (Exception e){
                    Log.i(TAG, "Network Response Error "+e.getMessage());

                }
                Log.i(TAG, "response "+ s);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String s = "";
//                try{
//                    if(error.networkResponse.data != null){
//                       // s = new String(error.networkResponse.data, "UTF-8");
//                    }
//                    Log.i(TAG, error.networkResponse.statusCode+"");
//                }
//                catch (Exception e){
//                    Log.i(TAG, "Network Error "+e.getMessage());
//                }

                Log.i(TAG, "Error "+error.networkResponse.statusCode);
                Log.i(TAG, "Error "+error.getNetworkTimeMs());
                Log.i(TAG, "Error "+error.getLocalizedMessage());
            }
        }) {
            @Override
            public String getBodyContentType() {
                return mimeType;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                dos = new DataOutputStream(bos);
                try {

//                   /*Token*/
//                    dos.writeBytes(lineEnd+twoHyphens + boundary + lineEnd);
//                    dos.writeBytes("Content-Disposition: form-data; name=\""+ APIKeys.kTokenFieldName.toString()+"\"" + lineEnd);
//                    dos.writeBytes(lineEnd);
//                    dos.writeBytes(mToken);
//
//                    /*Add Method*/
//                    dos.writeBytes(lineEnd+twoHyphens + boundary + lineEnd);
//                    dos.writeBytes("Content-Disposition: form-data; name=\""+ APIKeys.kAddMethodName .toString()+"\"" + lineEnd);
//                    dos.writeBytes(lineEnd);
//                    dos.writeBytes("add");

                    /*Image Type*/
                    dos.writeBytes(lineEnd+twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\""+ APIKeys.kImageTypeName .toString()+"\"" + lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(mContentType);



                   /*Image File*/
                    dos.writeBytes(lineEnd+twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\""+ APIKeys.kImageFieldName.toString()+"\";filename=\""
                            + "ic_action_file_attachment_light.png" + "\"" + lineEnd);
                    dos.writeBytes("Content-Type: image/png");
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(lineEnd);


                    ByteArrayInputStream fileInputStream = new ByteArrayInputStream(bitmapData);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    buffer = new byte[bufferSize];

                    // read file and write it into form...
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    while (bytesRead > 0) {
                        dos.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    }

//           /*Token*/
                    // send multipart form data necesssary after file data...
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + lineEnd);

                    return bos.toByteArray();


                } catch (IOException e) {
                    e.printStackTrace();
                }
                return bitmapData;
            }
        };

        Volley.newRequestQueue(context).add(baseVolleyRequest);

    }
}
