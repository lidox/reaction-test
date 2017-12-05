package com.artursworld.reactiontest.view.statistics;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.AxisValueFormatter;
import com.github.mikephil.charting.formatter.FormattedStringCache;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.text.DecimalFormat;

/**
 * This ValueFormatter is just for convenience and simply puts a "%" sign after
 * each value and removes decimal places.
 */
public class BarChartPercentFormatter implements ValueFormatter, AxisValueFormatter {

    protected FormattedStringCache.Generic<Integer, Float> mFormattedStringCache;
    protected FormattedStringCache.PrimFloat mFormattedStringCacheAxis;

    public BarChartPercentFormatter() {
        mFormattedStringCache = new FormattedStringCache.Generic<>(new DecimalFormat("###,###,##0"));
        mFormattedStringCacheAxis = new FormattedStringCache.PrimFloat(new DecimalFormat("###,###,##0"));
    }

    /**
     * Allow a custom decimal format
     *
     * @param format the format for the values
     */
    public BarChartPercentFormatter(DecimalFormat format) {
        mFormattedStringCache = new FormattedStringCache.Generic<>(format);
        mFormattedStringCacheAxis = new FormattedStringCache.PrimFloat(format);
    }

    // ValueFormatter
    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        return mFormattedStringCache.getFormattedValue(value, dataSetIndex) + " %";
    }

    // AxisValueFormatter
    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        return mFormattedStringCacheAxis.getFormattedValue(value) + " %";
    }

    @Override
    public int getDecimalDigits() {
        return 1;
    }

}
