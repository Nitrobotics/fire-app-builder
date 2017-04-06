/**
 * Copyright 2015-2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *     http://aws.amazon.com/apache2.0/
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.amazon.android.utils;

import com.amazon.utils.R;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A collection of utility methods, all static.
 */
public class Helpers {

    private static final String TAG = Helpers.class.getSimpleName();
    public static final boolean DEBUG = false;

    /**
     * Default charset to be used in app.
     */
    private static final String DEFAULT_CHARSET_TEXT = "UTF-8";

    /**
     * Making sure public utility methods remain static.
     */
    private Helpers() {

    }

    /**
     * Returns the screen/display size.
     *
     * @param context The context.
     * @return The display size.
     */
    public static Point getDisplaySize(Context context) {

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    /**
     * Shows a (long) toast.
     *
     * @param context The context.
     * @param msg     The message to display
     */
    public static void showToast(Context context, String msg) {

        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    /**
     * Shows a (long) toast.
     *
     * @param context    The context.
     * @param resourceId The resource id.
     */
    public static void showToast(Context context, int resourceId) {

        Toast.makeText(context, context.getString(resourceId), Toast.LENGTH_LONG).show();
    }

    /**
     * This method converts the dp to pixels.
     *
     * @param ctx The application context.
     * @param dp  The pixel value to convert to dp.
     * @return The rounded pixel result.
     */
    public static int convertDpToPixel(Context ctx, int dp) {

        float density = 0;
        try {
            density = ctx.getResources().getDisplayMetrics().density;
        }
        catch (Resources.NotFoundException exception) {
            Log.e(TAG, "Resources not found", exception);
        }
        return Math.round((float) dp * density);
    }

    /**
     * Converts a pixel value to dp value.
     *
     * @param ctx The application context.
     * @param px  The pixel value to convert to dp.
     * @return The rounded dp result.
     */
    public static int convertPixelToDp(Context ctx, int px) {

        float density = 0;
        try {
            density = ctx.getResources().getDisplayMetrics().density;
        }
        catch (Resources.NotFoundException exception) {
            Log.e(TAG, "Resources not found", exception);
        }
        return Math.round(px * density);
    }

    /**
     * Sleep for the given time in milliseconds
     *
     * @param milliseconds The time to sleep.
     */
    public static void sleep(int milliseconds) {

        try {
            Thread.sleep(milliseconds);
        }
        catch (InterruptedException e) {
            Log.e(TAG, "Thread sleep exception", e);
        }
    }

    /**
     * This method converts Unix time to Date.
     *
     * @param intDate Input integer value.
     * @return The date.
     */
    public static Date covertIntegerToDate(Integer intDate) {

        return new Date((long) intDate * 1000);
    }

    /**
     * Get the contents of the file and return as a Spanned text object.
     *
     * @param context  Application context that allows access to the assets folder.
     * @param filename Filename of the file in the assets folder.
     * @return The contents of the file as a Spanned text object.
     */
    public static String getContentFromFile(Context context, String filename) {

        StringBuilder stringBuilder = new StringBuilder();

        try {
            stringBuilder = new StringBuilder();
            InputStream inputStream = context.getResources().getAssets().open(filename);
            BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, Helpers
                    .getDefaultAppCharset()));
            String text;
            while ((text = in.readLine()) != null) {
                stringBuilder.append(text);
            }

            in.close();
        }
        catch (Resources.NotFoundException exception) {
            Log.e(TAG, "Resources not found", exception);
        }
        catch (IOException e) {
            Log.e(TAG, "Failed to load content from file " + filename, e);
        }

        return stringBuilder.toString();
    }

    /**
     * Checks for network connectivity.
     *
     * @param context The context to use to get hold of connection related data.
     * @return True if connected; false otherwise.
     */
    public static boolean isConnectedToNetwork(Context context) {

        final NetworkInfo networkInfo = ((ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        // Only care for a case where there is no network info at all or no network connectivity
        // detected
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * Rounds the corners of an image.
     *
     * @param activity The activity.
     * @param raw      The raw bitmap image to round.
     * @param round    The radius for the round corners.
     * @return The rounded image.
     */
    public static Bitmap roundCornerImage(Activity activity, Bitmap raw, float round) {

        int width = raw.getWidth();
        int height = raw.getHeight();
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        canvas.drawARGB(0, 0, 0, 0);

        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(ContextCompat.getColor(activity, android.R.color.black));

        final Rect rect = new Rect(0, 0, width, height);
        final RectF rectF = new RectF(rect);

        canvas.drawRoundRect(rectF, round, round, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(raw, rect, rect, paint);

        return result;
    }

    /**
     * This method updates the opacity of the bitmap.
     *
     * @param bitmap  The bitmap.
     * @param opacity The value of alpha.
     * @return The bitmap after adjusting the opacity.
     */
    public static Bitmap adjustOpacity(Bitmap bitmap, int opacity) {

        Bitmap mutableBitmap = bitmap.isMutable() ? bitmap : bitmap.copy(Bitmap.Config.ARGB_8888,
                                                                         true);
        Canvas canvas = new Canvas(mutableBitmap);
        int color = (opacity & 0xFF) << 24;
        canvas.drawColor(color, PorterDuff.Mode.DST_IN);
        return mutableBitmap;
    }

    /**
     * Handles the activity's enter fade transition.
     *
     * @param activity     The activity.
     * @param fadeDuration The fade duration in milliseconds.
     */
    public static void handleActivityEnterFadeTransition(Activity activity, int fadeDuration) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Transition changeTransform = TransitionInflater.from(activity).
                    inflateTransition(R.transition.change_image_transform);
            Transition fadeTransform = TransitionInflater.from(activity).
                    inflateTransition(android.R.transition.fade);
            fadeTransform.setStartDelay(0);
            fadeTransform.setDuration(fadeDuration);
            activity.getWindow().setSharedElementEnterTransition(changeTransform);
            activity.getWindow().setEnterTransition(fadeTransform);
        }
    }

    /**
     * Returns the charset to be used throughout the app. If the charset is invalid, default
     * charset from the system is returned.
     *
     * @return The charset to be used throughout the app.
     */
    public static Charset getDefaultAppCharset() {

        try {
            return Charset.forName(DEFAULT_CHARSET_TEXT);
        }
        catch (Exception e) {
            Log.e(TAG, "Illegal charset " + DEFAULT_CHARSET_TEXT + " given ", e);
            return Charset.defaultCharset();
        }
    }

    /**
     * Load a map of strings for the given JSON file. The file should be formatted as a flat
     * object with string key, value pairs, e.g.:
     *
     * {
     * "Key1", "Value1",
     * "Key2", "Value2"
     * }
     *
     * @param context    Context.
     * @param fileNameId File name ID of the file to read from.
     * @return The JSON file parsed as a map of strings. If there was an error while reading the
     * file such as the file not existing, an empty map is returned and the error is logged.
     */
    public static HashMap<String, String> loadStringMappingFromJsonFile(Context context,
                                                                        int fileNameId) {

        HashMap<String, String> result = new HashMap<>();
        String fileName = context.getString(fileNameId);
        try {
            if (FileHelper.doesFileExist(context, fileName)) {
                String fileData = FileHelper.readFile(context, fileName);
                Map map = JsonHelper.stringToMap(fileData);

                for (Object key : map.keySet()) {
                    result.put((String) key, String.valueOf(map.get(key)));
                }
            }
        }
        catch (Exception e) {
            Log.w(TAG, "Unable to read file " + fileName, e);
        }

        return result;
    }

    /**
     * Check that console output contains the specified text
     *
     * @param command    Console command
     * @param outputLine Text to check for
     * @return True if the output line was found in the logs from the given
     * command, false otherwise.
     */
    public static boolean checkConsole(String command, String outputLine) throws Exception {

        boolean stringFound = false;
        Process process = Runtime.getRuntime().exec(command);
        String line;
        BufferedReader bufferedReader =
                new BufferedReader(new InputStreamReader(process.getInputStream()));

        while ((line = bufferedReader.readLine()) != null) {
            if (line.contains(outputLine)) {
                stringFound = true;
                break;
            }

        }
        return stringFound;
    }

    /**
     * Clears LogCat messages
     *
     * @param delay A sleep delay after the logs are cleared.
     */
    public static void clearLogs(int delay) throws Exception {

        Runtime.getRuntime().exec("logcat -c");
        // allow logs to finish clearing, this is not instantaneous;
        Thread.sleep(delay);
    }
}