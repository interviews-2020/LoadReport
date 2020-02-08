import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class Main {

	// @SuppressWarnings("resource")
	public static void main(String[] args) throws Exception {

		String srcFile ="./LoanStats_securev1_2018Q4.csv";
		String destFile ="./perf_securev1_2018Q4.csv";
		if (args.length > 2) {
			System.out.println("The argument should be: ");
			System.out.println("[path of source file] [path of report file]");
			System.out.println(" OR  [path of source file]");
			System.out.println(" OR NO Argument");
			System.out.println("If NO Argument provided, 'LoanStats_securev1_2018Q4.csv' and 'perf_securev1_2018Q4.csv' will be default value");
			return;
		}else if(args.length ==2) {
			srcFile=args[0];
			destFile=args[1];
		}else if(args.length==1) {
			srcFile=args[0];
		}else {
			System.out.println("NO Argument provided, using 'LoanStats_securev1_2018Q4.csv' and 'perf_securev1_2018Q4.csv' to be sourceFile and reportFile");
		}
		
		System.out.println("srcFile:"+srcFile+"; destFile:"+destFile);
		
		DataProcessor dp = new DataProcessor();
		
		boolean status = dp.loadFile(srcFile);
		if(!status) {
			System.out.println("Error occurs while loading file; Exit");
			return ;
		}
		
		status = dp.outputReport(destFile);
		if(!status) {
			System.out.println("Error occurs while generating performance report; Exit");
			return ;
		}
		
		System.out.println("[SUCCESS]Loan Performance Report generated: "+destFile);

	}

}
