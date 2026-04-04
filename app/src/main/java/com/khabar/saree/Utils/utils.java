package com.khabar.saree.Utils;

import static android.content.Context.MODE_PRIVATE;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Looper;
import android.util.Log;

import androidx.core.content.FileProvider;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.khabar.saree.Model.ContentModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.os.Handler;   // ✅ CORRECT
import android.os.Looper;    // ✅ REQUIRED
import android.view.View;
import android.widget.Toast;

public class utils {

    public static void copyToClipboard(Context context, String text) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("label", text);
        clipboard.setPrimaryClip(clip);

        Toast.makeText(context, "تم النسخ", Toast.LENGTH_SHORT).show();
    }


    public static void shareMessage(Context c, String shareMessage){
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            //shareMessage = shareMessage + "\n\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            c.startActivity(Intent.createChooser(shareIntent, "choose one"));
        } catch(Exception e) {
            Toast.makeText(c, "خلل في المشاركة، الرجاء الإعادة", Toast.LENGTH_SHORT);
            //e.toString();
        }
    }
    public static void shareImageFromUrl(Context context, String imageUrl, String default_caption) {

        new Thread(() -> {
            try {
                URL url = new URL(imageUrl);
                InputStream input = url.openStream();

                File file = new File(context.getCacheDir(), "shared_image.jpg");
                FileOutputStream output = new FileOutputStream(file);

                byte[] buffer = new byte[1024];
                int len;

                while ((len = input.read(buffer)) != -1) {
                    output.write(buffer, 0, len);
                }

                output.close();
                input.close();

                Uri uri = FileProvider.getUriForFile(
                        context,
                        context.getPackageName() + ".provider",
                        file
                );

                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(() -> {
                    String caption = null;

                    try {
                        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);

                        if (clipboard != null && clipboard.hasPrimaryClip()) {
                            ClipData clipData = clipboard.getPrimaryClip();

                            if (clipData != null && clipData.getItemCount() > 0) {
                                CharSequence text = clipData.getItemAt(0).coerceToText(context);

                                if (text != null && text.length() > 0) {
                                    caption = text.toString();
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_STREAM, uri);

                    if(!default_caption.isEmpty()){
                        intent.putExtra(Intent.EXTRA_TEXT, default_caption);
                    }else if (caption != null) {
                        // ✅ Add caption if exists
                        intent.putExtra(Intent.EXTRA_TEXT, caption);
                    }




                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                    context.startActivity(Intent.createChooser(intent, "Share Image"));

                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static int getPositionByWord(ArrayList<ContentModel> newsList, String word) {
        for (int i = 0; i < newsList.size(); i++) {
            ContentModel item = newsList.get(i);
            if (item.getSource().contains(word)) { // or item.getContent().contains(word)
                return i+1; // Found match, return index
            }
        }
        return -1; // Not found
    }
    public static void saveData(Context c,ArrayList<ContentModel> list,String title) {
        SharedPreferences sharedPreferences = c.getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(title, json);
        editor.apply();
    }
    public static ArrayList<ContentModel> loadData(Context c, String title) {
        ArrayList<ContentModel> list;
        SharedPreferences sharedPreferences = c.getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(title, null);
        Type type = new TypeToken<ArrayList<ContentModel>>() {}.getType();
        list = gson.fromJson(json, type);
        if (list == null) {
            list = new ArrayList<>();
        }
        return list;
    }

    public static void saveSelected(Context c, int selected, String title) {
        SharedPreferences sharedPreferences = c.getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(selected);
        editor.putString(title, json);
        editor.apply();
    }

    public static int getSelected(Context c, String title) {
        int selected;
        SharedPreferences sharedPreferences = c.getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(title, null);
        Type type = new TypeToken<Integer>() {}.getType();
        if(gson.fromJson(json, type)!=null) {
            selected = gson.fromJson(json, type);
        }else{
            selected = 0;
        }
        return selected;
    }
    public static String getLine(int lineNumber,String text) {
        // Split the string by newline characters
        String[] lines = text.split("\\r?\\n");
        // Return the second line
        return lines.length > lineNumber ? lines[lineNumber] : "";
    }

    public static Result replaceWords(String text, String[] wordsToReplace, String[] replacementWords) {
        boolean wasReplaced = false;
        String modifiedText = text;
        for (int i = 0; i < wordsToReplace.length; i++) {
            if (modifiedText.contains(wordsToReplace[i])) {
                modifiedText = modifiedText.replace(wordsToReplace[i], replacementWords[i]);
                wasReplaced = true;
            }
        }
        return new Result(text,modifiedText, wasReplaced);
    }

    public static List<ContentModel> cleanarray(List<ContentModel> NewsList,String[] wordsToReplace,String[] replacementWords) {
        //String[] wordsToReplace = {"الجزيرة_مباشر","ترجمة قدس |","ترجمة قدس|","عاجل| ترجمة قدس:","شاهد | “فيديو” صادم..","(صور)"};
        //String[] replacementWords = {"","ترجمة عن الاعلام العبري:","ترجمة عن الاعلام العبري:","ترجمة عن الاعلام العبري :","",""};

        for (ContentModel item : NewsList) {
            if (item.getTitle() != null) {
                Log.e("cleaning_array", "cleanarray: "+item.getTitle() );
                utils.Result modifiedText = utils.replaceWords(item.getTitle(), wordsToReplace, replacementWords);
                item.setTitle(modifiedText.modifiedText);
            }
        }
        return NewsList;
    }

    public static class Result {
        public String modifiedText,mainText;
       public boolean wasReplaced;

        Result(String mainText,String modifiedText, boolean wasReplaced) {
            this.mainText = mainText;
            this.modifiedText = modifiedText;
            this.wasReplaced = wasReplaced;
        }
    }
    private void printArrayToLog(String[] array) {
        // Convert the array to a single string
        StringBuilder sb = new StringBuilder();
        for (String s : array) {
            sb.append(s).append("\n");
        }

        // Log the result
        Log.d("bayanhizbmodifyarray", sb.toString());
    }

    public static List<Integer> extractNumbers(String text) {
        List<Integer> numbers = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\d+"); // Regular expression to match numbers
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            numbers.add(Integer.parseInt(matcher.group()));
        }

        return numbers;
    }

    public static boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static String findLargestString(String[] array) {
        String largest = "";
        for (String s : array) {
            if (s.length() > largest.length()) {
                largest = s;
            }
        }
        return largest;
    }


    public static void saveNumber(Context context, int number) {
        String PREFS_NAME = "my_prefs";
        String KEY_NUMBER = "key_number";
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_NUMBER, number);
        editor.apply(); // Save changes
    }
    public static int getNumber(Context context, int defaultValue) {
        String PREFS_NAME = "my_prefs";
        String KEY_NUMBER = "key_number";
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(KEY_NUMBER, defaultValue); // Return default if not found
    }

    public static List<String> searchForIraqWord(String text, String word) {
        List<String> resultLines = new ArrayList<>();
        String[] lines = text.split("\n");
        for (String line : lines) {
            if (line.contains(word) && line.contains("العراق")) {
                resultLines.add(line);
            }
        }
        return resultLines;
    }
    public static List<String> searchForWord(String text, String word) {
        List<String> resultLines = new ArrayList<>();
        String[] lines = text.split("\n");
        for (String line : lines) {
            if (line.contains(word)) {
                resultLines.add(line);
            }
        }
        return resultLines;
    }


}
