/*
 *******************************************************************************
 * Copyright (C) 2007-2009, International Business Machines Corporation and    *
 * others. All Rights Reserved.                                                *
 *******************************************************************************
 */
package com.ibm.icu.dev.test.duration;

import java.text.FieldPosition;
import java.util.Date;
import java.util.MissingResourceException;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;

import com.ibm.icu.dev.test.TestFmwk;
import com.ibm.icu.text.DurationFormat;
import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.ULocale;

/**
 * @author srl
 *
 */
public class ICUDurationTest extends TestFmwk {

    /**
     * 
     */
    public ICUDurationTest() {
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        new ICUDurationTest().run(args);
    }
    
    
    /**
     * Basic test
     */
    public void TestBasics() {
        DurationFormat df;
        String expect;
        String formatted;
        
        df = DurationFormat.getInstance(new ULocale("it"));
        formatted = df.formatDurationFromNow(4096);
        expect = "fra quattro secondi";
        if(!expect.equals(formatted)) {
            errln("Expected " + expect + " but got " + formatted);
        } else {
            logln("format duration -> " + formatted);
        }
        
        formatted = df.formatDurationFromNowTo(new Date(0));
        Calendar cal = Calendar.getInstance();
        int years = cal.get(Calendar.YEAR) - 1970; // year of Date(0)
        expect = "fra " + years + " anni";
        if(!expect.equals(formatted)) {
            errln("Expected " + expect + " but got " + formatted);
        } else {
            logln("format date  -> " + formatted);
        }
        
        formatted = df.formatDurationFrom(1000*3600*24, new Date(0).getTime());
        expect = "fra un giorno";
        if(!expect.equals(formatted)) {
            errln("Expected " + expect + " but got " + formatted);
        } else {
            logln("format date from -> " + formatted);
        }

        formatted = df.format(new Long(1000*3600*24*2));
        expect = "fra due giorni";
        if(!expect.equals(formatted)) {
            errln("Expected " + expect + " but got " + formatted);
        } else {
            logln("format long obj -> " + formatted);
        }
    }

    public void TestSimpleXMLDuration() {
        DatatypeFactory factory = null;
        try {
            factory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            errln("Error instantiating XML DatatypeFactory.");
            e.printStackTrace();
        }
        
        Duration d;
        DurationFormat df;
        String out;
        String expected;
        String expected2;
        
        // test 1
        d = factory.newDuration("PT2H46M40S");
        df = DurationFormat.getInstance(new ULocale("en"));
        expected = "2 hours, 46 minutes, and 40 seconds";
        out = df.format(d);
        if(out.equals(expected)) {
            logln("out=expected: " + expected + " from " + d);
        } else {
            errln("FAIL: got " + out + " wanted " + expected + " from " + d);
        }
        
        // test 2
        d = factory.newDuration(10000);
        df = DurationFormat.getInstance(new ULocale("en"));
        expected = "10 seconds";
        out = df.format(d);
        if(out.equals(expected)) {
            logln("out=expected: " + expected + " from " + d);
        } else {
            errln("FAIL: got " + out + " wanted " + expected + " from " + d);
        }
        // test 3
        d = factory.newDuration("P0DT0H0M10.0S");
        df = DurationFormat.getInstance(new ULocale("en"));
        expected = "10 seconds";
        out = df.format(d);
        if(out.equals(expected)) {
            logln("out=expected: " + expected + " from " + d);
        } else {
            errln("FAIL: got " + out + " wanted " + expected + " from " + d);
        }
        // test 4
        d = factory.newDuration(86400000);
        df = DurationFormat.getInstance(new ULocale("en"));
        expected = "1 day, 0 hours, 0 minutes, and 0 seconds";
        expected2 = "1 day and 0 seconds"; // This is the expected result for Windows with IBM JRE6
        out = df.format(d);
        if(out.equals(expected)) {
            logln("out=expected: " + expected + " from " + d);
        } else {
            if(out.equals(expected2)){
                logln("WARNING: got " + out + " wanted " + expected + " from " + d);
            } else{
                errln("FAIL: got " + out + " wanted " + expected + " from " + d);
            }
        }
    }


    public void TestXMLDuration() {
        DatatypeFactory factory = null;
        try {
            factory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            errln("Error instantiating XML DatatypeFactory.");
            e.printStackTrace();
        }
        
        String cases[] = {
                "en",   "PT10.00099S",   "10 seconds",
                "en",   "#10000",   "10 seconds",
                "en",   "-PT10.00099S",   "10 seconds",
                "en",   "#-10000",   "10 seconds",
                
                // from BD req's
                "en",   "PT2H46M40S",   "2 hours, 46 minutes, and 40 seconds",
                "it",   "PT2H46M40S",   "due ore, 46 minuti e 40 secondi",
                
                // more cases
                "en",   "PT10S",        "10 seconds",
                "en",   "PT88M70S",        "88 minutes and 70 seconds",
                "en",   "PT10.100S",    "10 seconds and 100 milliseconds",
                "en",   "-PT10S",       "10 seconds",
                "en",   "PT0H5M0S",     "5 minutes and 0 seconds"
        };
        
        for(int n=0;n<cases.length;n+=3) {
            String loc = cases[n+0];
            String from = cases[n+1];
            String to = cases[n+2];
            
            ULocale locale = new ULocale(loc);
            Duration d;
            if(from.startsWith("#")) {
                d = factory.newDuration(Long.parseLong(from.substring(1)));
            } else {
                d = factory.newDuration(from);
            }
            
            DurationFormat df = DurationFormat.getInstance(locale);
            String output = df.format(d);
            
            if(output.equals(to)) {
                logln("SUCCESS: locale: " + loc + ", from " + from + " ["+d.toString()+"] " +" to " + to + "= " + output);
            } else {
                logln("FAIL: locale: " + loc + ", from " + from + " ["+d.toString()+"] " +": expected " + to + " got " + output);
            }
        }
    }


    public void TestBadObjectError() {
        Runtime r = Runtime.getRuntime();
        DurationFormat df = DurationFormat.getInstance(new ULocale("en"));
        String output = null;
        try {
            output = df.format(r);
            errln("FAIL: did NOT get IllegalArgumentException! Should have. Formatted Runtime as " + output + " ???");
        } catch (IllegalArgumentException iae) {
            logln("PASS: expected: Caught iae: " + iae.toString() );
        }
        // try a second time, because it is a different code path for java < 1.5
        try {
            output = df.format(r);
            errln("FAIL: [#2] did NOT get IllegalArgumentException! Should have. Formatted Runtime as " + output + " ???");
        } catch (IllegalArgumentException iae) {
            logln("PASS: [#2] expected: Caught iae: " + iae.toString() );
        }
    }

    public void TestBadLocaleError() {
        try {
            DurationFormat df = DurationFormat.getInstance(new ULocale("und"));
            df.format(new Date());
            logln("Should have thrown err.");
            errln("failed, should have thrown err.");
        } catch(MissingResourceException mre) {
            logln("PASS: caught missing resource exception on locale 'und'");
            logln(mre.toString());
        }
    }

    public void TestResourceWithCalendar() {
        DurationFormat df = DurationFormat.getInstance(new ULocale("th@calendar=buddhist"));
        // should pass, but return a default formatter for th.
        if (df == null) {
            errln("FAIL: null DurationFormat returned.");
        }
    }
    
    /* Tests the class
     *      DurationFormat
     */
    public void TestDurationFormat(){
        @SuppressWarnings("serial")
        class TestDurationFormat extends DurationFormat {
            public StringBuffer format(Object object, StringBuffer toAppend, FieldPosition pos) {return null;}
            public String formatDurationFrom(long duration, long referenceDate) {return null;}
            public String formatDurationFromNow(long duration) {return null;}
            public String formatDurationFromNowTo(Date targetDate) {return null;}
            public TestDurationFormat() {super();}
            
        }
        
        // Tests the constructor and the following method
        //      public Object parseObject(String source, ParsePosition pos)
        try{
            TestDurationFormat tdf = new TestDurationFormat();
            tdf.parseObject("",null);
            errln("DurationFormat.parseObjet(String,ParsePosition) was " +
                    "to return an exception for an unsupported operation.");
        } catch(Exception e){}
    }
}
