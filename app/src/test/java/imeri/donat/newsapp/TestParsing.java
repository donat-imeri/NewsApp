package imeri.donat.newsapp;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.util.Xml;

import com.google.android.gms.common.util.IOUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;

@RunWith(PowerMockRunner.class)
@PrepareForTest(MainActivity.class)
public class TestParsing {
    private MainActivity mActivityTest;

    @Before
    public void setUp() throws Exception {
        mActivityTest=new MainActivity();
    }


    @Test
    public void testParsingFunction() throws IOException, XmlPullParserException {
        /*MainActivity xml=mock(MainActivity.class);
        Xml x=mock(Xml.class);
        when(Xml.newPullParser()).thenReturn(Xml.newPullParser());
        */
        PowerMockito.mockStatic(Xml.class);

        PowerMockito.when(Xml.newPullParser())
                .thenReturn(Xml.newPullParser());
        InputStream targetStream = this.getClass().getClassLoader().getResourceAsStream("testdata.txt");
        //String result = CharStreams.toString(new InputStreamReader(targetStream, Charsets.UTF_8));
        mActivityTest.parseFeed(targetStream);



    }

}
