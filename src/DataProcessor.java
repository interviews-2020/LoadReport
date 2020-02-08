import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;


public class DataProcessor {

	PerfReport report=null;

	public DataProcessor() {
		report =  new PerfReport();
	}

	public boolean loadFile(String file) {
		String[] nextRecord;
		int bcount = 0;
		try {
			Reader reader = Files.newBufferedReader(Paths.get(file));
			CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build();
			// Reading Records One by One in a String array

			nextRecord = csvReader.readNext();
			report.validateColumnNames(nextRecord);
				
			//int i = 0;
			while ((nextRecord = csvReader.readNext()) != null) {
				if (nextRecord.length<3) {
					bcount++;
					continue;
				}

				//System.out.println("Line " + (i++) + "==========================");
				Record rec =new Record();
				rec.build(nextRecord);
				report.addRecord(rec);

			}
		} catch (Exception e) {
			System.out.println(e);
			return false;
		}

		System.out.println("bad row count: " + bcount);
		return true;
	}

	public boolean outputReport(String file) {
		try {

			FileOutputStream fos = new FileOutputStream(file);
			OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
			CSVWriter writer = new CSVWriter(osw);
			writer.writeNext(PerfReport.PERF_REPORT_COLUMNS);
			
			List<String[]> reps= report.generateReport();
			for (String[] entry : reps) {
				writer.writeNext(entry);
			}

			writer.close();
			
		}catch(Exception e) {
			System.out.println(e);
			return false;
		}
		
		return true;
	}
}
