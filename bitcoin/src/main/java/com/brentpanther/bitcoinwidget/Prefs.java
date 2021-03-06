package com.brentpanther.bitcoinwidget;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;


class Prefs {

    private static final String LAST_UPDATE = "last_update";
    private static final String CURRENCY = "currency";
    private static final String CURRENCY_CUSTOM = "currency_custom";
    private static final String REFRESH = "refresh";
    private static final String PROVIDER = "provider";
    private static final String EXCHANGE = "exchange";
    private static final String SHOW_LABEL = "show_label";
    private static final String THEME = "theme";
    private static final String HIDE_ICON = "icon";
    private static final String SHOW_DECIMALS = "show_decimals";
    private static final String LAST_VALUE = "last_value";
    private static final String UNITS = "units";
    private static final String COIN = "coin";
    private static final String COIN_CUSTOM = "coin_custom";
    private static final String PORTRAIT_TEXT_SIZE = "portrait_text_size";
    private static final String LANDSCAPE_TEXT_SIZE = "landscape_text_size";
    private final int widgetId;
    private final Context context;

    Prefs(int widgetId) {
        this.context = WidgetApplication.getInstance();
        this.widgetId = widgetId;
    }

    int getWidgetId() {
        return widgetId;
    }

    private SharedPreferences getPrefs() {
        return context.getSharedPreferences(context.getString(R.string.key_prefs), Context.MODE_PRIVATE);
    }

    Coin getCoin() {
        String coin = getValue(COIN);
        return coin != null ? Coin.valueOf(coin) : Coin.BTC;
    }

    String getExchangeCoinName() {
        String coin = getValue(COIN_CUSTOM);
        return coin != null ? coin : getCoin().name();
    }

    String getCurrency() {
        return getValue(CURRENCY);
    }

    String getExchangeCurrencyName() {
        String code = getValue(CURRENCY_CUSTOM);
        return code != null ? code : getValue(CURRENCY);
    }

    int getInterval() {
        String value = getValue(REFRESH);
        if (value == null) return 30;
        return Integer.valueOf(value);
    }

    Exchange getExchange() {
        String value = getValue(EXCHANGE);
        if (value == null) {
            return Exchange.values()[0];
        }
        return Exchange.valueOf(value);
    }

    String getExchangeName() {
        return getValue(EXCHANGE);
    }

    int getThemeLayout() {
        String value = getValue(THEME);
        if (value == null) return R.layout.widget_layout;
        switch(value) {
            case "Dark":
                return R.layout.widget_layout_dark;
            case "Transparent Dark":
                return R.layout.widget_layout_transparent_dark;
            case "DayNight":
                return R.layout.widget_layout_auto;
            case "Transparent DayNight":
                return R.layout.widget_layout_transparent_auto;
            case "Transparent":
                return R.layout.widget_layout_transparent;
            default:
                return R.layout.widget_layout;
        }
    }

    boolean isTransparent() {
        String value = getValue(THEME);
        return "Transparent".equals(value) || "Transparent Dark".equals(value) || "Transparent DayNight".equals(value);
    }

    boolean isLightTheme(Context context) {
        int themeLayout = getThemeLayout();
        if (themeLayout == R.layout.widget_layout_auto || themeLayout == R.layout.widget_layout_transparent_auto) {
            int nightModeFlags = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            return nightModeFlags == Configuration.UI_MODE_NIGHT_NO;
        }
        return themeLayout == R.layout.widget_layout || themeLayout == R.layout.widget_layout_transparent;
    }

    String getUnit() {
        return getValue(UNITS);
    }

    long getLastUpdate() {
		String value = getValue(LAST_UPDATE);
        if(value==null) return 0;
        return Long.valueOf(value);
	}

	void setLastUpdate() {
        setValue(LAST_UPDATE, "" + System.currentTimeMillis());
	}

    void setLastValue(String value) {
        setValue(LAST_VALUE, value);
    }

    String getLastValue() {
        return getValue(LAST_VALUE);
    }

    boolean getLabel() {
        return Boolean.valueOf(getValue(SHOW_LABEL));
    }

    boolean showIcon() {
        return !Boolean.valueOf(getValue(HIDE_ICON));
    }

    boolean getShowDecimals() {
        String value = getValue(SHOW_DECIMALS);
        if (value == null) {
            return true;
        }
        return Boolean.valueOf(value);
    }

    void setValue(String key, String value) {
        String string = getPrefs().getString("" + widgetId, null);
        JsonObject obj = new JsonObject();
        if (string != null) {
            obj = new Gson().fromJson(string, JsonObject.class);
        }
        obj.addProperty(key, value);
        getPrefs().edit().putString("" + widgetId, obj.toString()).apply();
    }

    void setValues(String coin, String currency, int refreshValue, String exchange, boolean checked,
                   String theme, boolean iconChecked, boolean showDecimals, String unit) {
        JsonObject obj = new JsonObject();
        obj.addProperty(COIN, coin);
        obj.addProperty(CURRENCY, currency);
        obj.addProperty(REFRESH, "" + refreshValue);
        obj.addProperty(EXCHANGE, exchange);
        obj.addProperty(SHOW_LABEL, "" + checked);
        obj.addProperty(THEME, theme);
        obj.addProperty(HIDE_ICON, "" + !iconChecked);
        obj.addProperty(SHOW_DECIMALS, "" + showDecimals);
        obj.addProperty(UNITS, unit);
        getPrefs().edit().putString("" + widgetId, obj.toString()).apply();
	}

    void delete() {
		getPrefs().edit().remove("" + widgetId).apply();
	}

    String getValue(String key) {
        String string = getPrefs().getString("" + widgetId, null);
        if (string == null) return null;
        JsonObject obj = new Gson().fromJson(string, JsonObject.class);
        if (!obj.has(key)) return null;
        JsonElement el = obj.get(key);
        return el.isJsonNull() ? null : el.getAsString();
    }

    void setTextSize(float size, boolean portrait) {
        setValue(portrait ? PORTRAIT_TEXT_SIZE : LANDSCAPE_TEXT_SIZE, Float.toString(size));
    }

    float getTextSize(boolean portrait) {
        String size = getValue(portrait ? PORTRAIT_TEXT_SIZE : LANDSCAPE_TEXT_SIZE);
        if (size == null) {
            return Float.MAX_VALUE;
        }
        float textSize = Float.valueOf(size);
        return textSize > 0 ? textSize : Float.MAX_VALUE;
    }

    void clearTextSize() {
        setTextSize(0, true);
        setTextSize(0, false);
    }

    void setExchangeValues(String exchangeCoinName, String exchangeCurrencyName) {
        if (exchangeCoinName != null) setValue(COIN_CUSTOM, exchangeCoinName);
        if (exchangeCurrencyName != null) setValue(CURRENCY_CUSTOM, exchangeCurrencyName);
    }
}
