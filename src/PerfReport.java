import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

enum Index{
	loan_amnt("loan_amnt",2),
	int_rate("int_rate",6),
	grade("grade",8),
	loan_status("loan_status", 16),
	out_prncp("out_prncp",38),
	total_rec_prncp("total_rec_prncp",42),
	total_rec_int("total_rec_int",43),
	recoveries("recoveries", 45),
	
;
	
	public final String name;
	public final int id;
	
	Index(String v, int i){
		this.name=v;
		this.id=i;
	}
	
};

class ReportEntry{
	
	public String grade;
	public double total_issued;
	public double fully_paid;
	public double current;
	public double late;
	public double charged_off_net;
	public double prn_pymnt_rec;
	public double inv_pymnt_rec;
	public double avg_inv_rate;
	public double adj_net_annual_return;
	
	public double rate_weighted_accu;
	
	private static DecimalFormat df = new DecimalFormat("0.00");

	
	public void mergeRecord(Record rec) {
		if(!rec.isValid()) return;
		this.grade = rec.grade;
		this.total_issued += rec.loan_amnt;
		
		if(rec.loan_status.equalsIgnoreCase("Fully Paid")) {
			this.fully_paid += rec.loan_amnt;			
		}else if(rec.loan_status.equalsIgnoreCase("Current") || rec.loan_status.equalsIgnoreCase("In Grace Period")) {
			this.current += rec.out_prncp;
		}else if(rec.loan_status.equalsIgnoreCase("Late (16-30 days)") || 
				rec.loan_status.equalsIgnoreCase("Late (31-120 days)") ||
				rec.loan_status.equalsIgnoreCase("Default")
				) {
			this.late += rec.out_prncp;
		}else if(rec.loan_status.equalsIgnoreCase("Charged Off")) {
			//to do: diff
			this.charged_off_net += (rec.loan_amnt- rec.total_rec_prncp- rec.recoveries);
		}else  {
		   //to do: not quite sure how to handle this data
			System.out.println("Non-recognized load status :"+rec.loan_status);
		}
		
		this.prn_pymnt_rec += (rec.total_rec_prncp + rec.recoveries);
		
		//to do: diff 
		this.inv_pymnt_rec += rec.total_rec_int;
		this.rate_weighted_accu += rec.rate_weighted_accu;
	}
	
	
	public String[] toReportEntry() {
		String[] rep =new String[PerfReport.PERF_REPORT_COLUMNS.length];
		rep[0] = this.grade;
		rep[1] = String.format("%,d",(long)this.total_issued);
		rep[2] = String.format("%,d",(long)this.fully_paid);
		rep[3] = String.format("%,d",(long)this.current);
		rep[4] = String.format("%,d",(long)this.late);
		rep[5] = String.format("%,d",(long)this.charged_off_net);
		rep[6] = String.format("%,d",(long)this.prn_pymnt_rec);
		rep[7] = String.format("%,d",(long)this.inv_pymnt_rec);
		rep[8] = df.format(this.total_issued==0? 0: this.rate_weighted_accu/this.total_issued*100)+"%";
		rep[9] = "0%";
		
		return rep;
	}
	
}

public class PerfReport {
	
	public static final String[] PERF_REPORT_COLUMNS = { "", "TOTAL ISSUED","FULLY PAID","CURRENT","LATE","CHARGED OFF (NET)","PRINCIPAL PAYMENTS RECEIVED","INTEREST PAYMENTS RECEIVED","AVG. INTEREST RATE","ADJ. NET ANNUALIZED RETURN" };
	public static final String[] PERF_REPORT_ROWS = { "A", "B","C","D","E","FG","All"};
	
	private HashMap<String, ReportEntry> mapByGrade = null;
	
	public PerfReport() {
		mapByGrade = new HashMap<String, ReportEntry>();
		for(String grade: PERF_REPORT_ROWS) {
			mapByGrade.put(grade, new ReportEntry());
		}
	}
	
	public void validateColumnNames(String[] nextRecord) {
		assert (nextRecord[Index.loan_amnt.id].trim().equalsIgnoreCase(Index.loan_amnt.name));
		assert (nextRecord[Index.grade.id].trim().equalsIgnoreCase(Index.grade.name));
		assert (nextRecord[Index.loan_status.id].trim().equalsIgnoreCase(Index.loan_status.name));
		assert (nextRecord[Index.total_rec_prncp.id].trim().equalsIgnoreCase(Index.total_rec_prncp.name));
		assert (nextRecord[Index.total_rec_int.id].trim().equalsIgnoreCase(Index.total_rec_int.name));
		assert (nextRecord[Index.int_rate.id].trim().equalsIgnoreCase(Index.int_rate.name));
		assert (nextRecord[Index.recoveries.id].trim().equalsIgnoreCase(Index.recoveries.name));
		assert (nextRecord[Index.out_prncp.id].trim().equalsIgnoreCase(Index.out_prncp.name));
		
	}
	
	
	
	public void addRecord(Record rec) {
		//if(nextRecord==null || nextRecord.length<2) return;
		if(!rec.isValid()) return;
		ReportEntry entry = mapByGrade.get(rec.grade);
		if(entry==null) {
			System.out.println("Map not have key:"+rec.grade);
			return;
		}
		
		entry.mergeRecord(rec);
		
		//load record into all row as well
		entry = mapByGrade.get("All");	
		entry.mergeRecord(rec);
		
	}
	
	public List<String[]> generateReport() {
		List<String[]> list =new ArrayList<String[]>();
        for(String key : PerfReport.PERF_REPORT_ROWS) {
        	list.add(mapByGrade.get(key).toReportEntry());
        }
		return list;
	}

}
