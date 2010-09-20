package com.ldodds.slug.http;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import com.ldodds.slug.framework.Task;

public interface URLTask extends Task
{
	URL getURL();
	int getDepth();
    HttpURLConnection openConnection() throws IOException;
}
