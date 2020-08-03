package com.dpcsa.compon.network;

import android.content.Context;
import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CacheWork {
    public Map<String, CacheParam> mapCache;
    private long currentTime;
    private int sizeCache, maxSizeCache, maxSizeFile;
    private Context context;
    private File cacheDir;
    private String prefix = "simple";
    private String suffix = ".jsn";

    public CacheWork(Context context) {
        mapCache = new HashMap<>();
        sizeCache = 0;
        maxSizeCache = 2000000;
        maxSizeFile = 500000;
        this.context = context;
        cacheDir = new File(context.getFilesDir() + "/simpleCache");
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
    }


    public boolean addCasche(String name, long duration, String json) {
        int len = json.length();
        if (len > maxSizeFile) {
            return false;
        }
        CacheParam cp = mapCache.get(name);
        currentTime = new GregorianCalendar().getTimeInMillis();
        if (cp != null) {
            deleteFile(cp.nameFile);
            sizeCache -= cp.length;
            if (sizeCache < 0) {
                sizeCache = 0;
            }
            mapCache.remove(name);
        }
        if (maxSizeCache - sizeCache < len) {
            List<NameLen> nl = new ArrayList<>();
            for (String key : mapCache.keySet()) {
                nl.add(new NameLen(key, mapCache.get(key).time));
            }
            Collections.sort(nl, new Comparator<NameLen>() {
                @Override
                public int compare(NameLen o1, NameLen o2) {
                    if(o1.duration > o2.duration) return 1;
                    if(o1.duration < o2.duration) return -1;
                    return 0;
                }
            });
//            for (int i = 0; i < nl.size(); i++) {
//                Log.d("QWERT","Name="+nl.get(i).duration+" DDD="+nl.get(i).name);
//            }
            while (maxSizeCache - sizeCache < len) {
                deleteFile(cp.nameFile);
                sizeCache -= cp.length;
                if (sizeCache < 0) {
                    sizeCache = 0;
                }
                mapCache.remove(name);
            }
        }
        mapCache.put(name, new CacheParam(currentTime + duration, len, writeFile(json), duration));
//        writeFile(name, json);
        return true;
    }

    public String getJson(String name) {
        CacheParam cp = mapCache.get(name);
        if (cp != null) {
            if (cp.time > new GregorianCalendar().getTimeInMillis() || cp.duration == 1) {
                return readFile(cp.nameFile);
            } else {
                deleteFile(cp.nameFile);
                sizeCache -= cp.length;
                mapCache.remove(name);
                return null;
            }
        } else {
            return null;
        }
    }

    @NonNull
    private String readFile(String fileName) {
//        File file = new File(cacheDir + "/" + fileName);
        File file = new File(fileName);
        StringBuilder stringBuilder = new StringBuilder();
        try {
            FileInputStream inputStream = new FileInputStream(file.getAbsolutePath());
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            try {
                while ((line = bufferedReader.readLine()) != null){
                    stringBuilder.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    private String writeFile(String data) {
//        File file = new File(cacheDir, fileName);
//        File file = File.createTempFile(prefix, suffix, cacheDir);
        String name = "";
        try {
            File file = File.createTempFile(prefix, suffix, cacheDir);
            name = file.getAbsolutePath();
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file.getAbsolutePath());
            outputStream.write(data.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return name;
    }

    private void deleteFile(String fileName) {
//        File file = new File(cacheDir + "/" + fileName);
        File file = new File(fileName);
        file.delete();
    }

    private class NameLen {
        String name;
        long duration;
        public NameLen (String n, long d) {
            name = n;
            duration = d;
        }
    }

    public class CacheParam {
        public long time;
        public long duration;
        public int length;
        public String nameFile;

        public CacheParam(long time, int length, String nameFile, long duration) {
            this.length = length;
            this.time = time;
            this.nameFile = nameFile;
            this.duration = duration;
        }
    }
}
