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

    String lineEnd = "\r\n";
    String boundary = "apiclient-" + System.currentTimeMillis();
    String twoHyphens = "--";


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


    public void sendFile(Bitmap bitmap, String token, String contentType, Context context) {
        //Prepare request

        final String mToken = token;
        final String mContentType = contentType;

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
                if( error.networkResponse != null){
                    Log.i(TAG, "Error "+error.networkResponse.statusCode);

                }
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
                //Check the details of sending the post request: http://www.w3.org/TR/html401/interact/forms.html#form-data-set
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                DataOutputStream dos = new DataOutputStream(bos);
                byte[] buffer;
                int maxBufferSize = 1024 * 1024;
                int bytesRead, bytesAvailable, bufferSize;
                String lineStart = lineEnd+twoHyphens+boundary+lineEnd;


                try {

                   /*Token*/
                    dos.writeBytes(lineStart);
                    dos.writeBytes("Content-Disposition: form-data; name=\""+ APIKeys.kTokenFieldName.toString()+"\"" + lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(mToken);

                    /*Add Method*/
                    dos.writeBytes(lineStart);
                    dos.writeBytes("Content-Disposition: form-data; name=\""+ APIKeys.kAddMethodName .toString()+"\"" + lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes("add");

                    /*Image Type*/
                    dos.writeBytes(lineStart);
                    dos.writeBytes("Content-Disposition: form-data; name=\""+ APIKeys.kImageTypeName .toString()+"\"" + lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(mContentType);

                   /*Image File*/
                    dos.writeBytes(lineStart);
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

                    dos.writeBytes(lineEnd);
                    dos.writeBytes(lineEnd+twoHyphens+ boundary +lineEnd);

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
