package test;

import java.io.BufferedReader;

import com.google.gson.Gson;

import download.DownloadHelper;
import download.iex.IntraDayData;

public class Test {
	public static void main(String[] args) throws Exception {
		BufferedReader br = DownloadHelper.getBufferedReaderFromURL("https://api.iextrading.com/1.0/stock/aapl/chart/date/20171201");
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}
		br.close();
		Gson gson = new Gson();
		IntraDayData[] dataArray = gson.fromJson(sb.toString(), IntraDayData[].class);
		for (IntraDayData data : dataArray) {
			System.out.println(data.getDate());
		}
	}
}
