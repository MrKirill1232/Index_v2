package com.index.data.utils;

import com.index.IndexMain;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Properties;

public class PropertiesParser {

    private final File _file;
    private final Properties _properties = new Properties();

    public PropertiesParser(String name)
    {
        this(new File(name));
    }

    public PropertiesParser(File file)
    {
        _file = file;
        try (FileInputStream fileInputStream = new FileInputStream(file))
        {
            try (InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, Charset.defaultCharset()))
            {
                _properties.load(inputStreamReader);
            }
        }
        catch (Exception e)
        {
            sendMessage("[" + _file.getName() + "] There was an error loading config reason: " + e.getMessage());
        }
    }

    protected void sendMessage(String message){
        new IndexMain().SendAnswer(new IndexMain().YummyReChat, "Index", message, "null", 0);
    }

    public boolean containskey(String key)
    {
        return _properties.containsKey(key);
    }

    private String getValue(String key)
    {
        final String value = _properties.getProperty(key);
        return value != null ? value.trim() : null;
    }

    public boolean getBoolean(String key, boolean defaultValue)
    {
        final String value = getValue(key);
        if (value == null)
        {
            sendMessage("[" + _file.getName() + "] missing property for key: " + key + " using default value: " + defaultValue);
            return defaultValue;
        }

        if (value.equalsIgnoreCase("true"))
        {
            return true;
        }
        else if (value.equalsIgnoreCase("false"))
        {
            return false;
        }
        else
        {
            sendMessage("[" + _file.getName() + "] Invalid value specified for key: " + key + " specified value: " + value + " should be \"boolean\" using default value: " + defaultValue);
            return defaultValue;
        }
    }

    public byte getByte(String key, byte defaultValue)
    {
        final String value = getValue(key);
        if (value == null)
        {
            sendMessage("[" + _file.getName() + "] missing property for key: " + key + " using default value: " + defaultValue);
            return defaultValue;
        }

        try
        {
            return Byte.parseByte(value);
        }
        catch (NumberFormatException e)
        {
            sendMessage("[" + _file.getName() + "] Invalid value specified for key: " + key + " specified value: " + value + " should be \"byte\" using default value: " + defaultValue);
            return defaultValue;
        }
    }

    public short getShort(String key, short defaultValue)
    {
        final String value = getValue(key);
        if (value == null)
        {
            sendMessage("[" + _file.getName() + "] missing property for key: " + key + " using default value: " + defaultValue);
            return defaultValue;
        }

        try
        {
            return Short.parseShort(value);
        }
        catch (NumberFormatException e)
        {
            sendMessage("[" + _file.getName() + "] Invalid value specified for key: " + key + " specified value: " + value + " should be \"short\" using default value: " + defaultValue);
            return defaultValue;
        }
    }

    public int getInt(String key, int defaultValue)
    {
        final String value = getValue(key);
        if (value == null)
        {
            sendMessage("[" + _file.getName() + "] missing property for key: " + key + " using default value: " + defaultValue);
            return defaultValue;
        }

        try
        {
            return Integer.parseInt(value);
        }
        catch (NumberFormatException e)
        {
            sendMessage("[" + _file.getName() + "] Invalid value specified for key: " + key + " specified value: " + value + " should be \"int\" using default value: " + defaultValue);
            return defaultValue;
        }
    }

    public long getLong(String key, long defaultValue)
    {
        final String value = getValue(key);
        if (value == null)
        {
            sendMessage("[" + _file.getName() + "] missing property for key: " + key + " using default value: " + defaultValue);
            return defaultValue;
        }

        try
        {
            return Long.parseLong(value);
        }
        catch (NumberFormatException e)
        {
            sendMessage("[" + _file.getName() + "] Invalid value specified for key: " + key + " specified value: " + value + " should be \"long\" using default value: " + defaultValue);
            return defaultValue;
        }
    }
    public String getString(String key, String defaultValue)
    {
        final String value = getValue(key);
        if (value == null)
        {
            sendMessage("[" + _file.getName() + "] missing property for key: " + key + " using default value: " + defaultValue);
            return defaultValue;
        }
        return value;
    }
}
