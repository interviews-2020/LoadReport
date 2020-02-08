

public class Record {
	
	private boolean isValid;
	
	public double loan_amnt;
	public double int_rate;
	public String grade;
	public String loan_status;
	public double out_prncp;
	public double total_rec_prncp;
	public double total_rec_int;
	public double recoveries;
	
	
	public double rate_weighted_accu;
	
	public Record() {
		isValid= false;
	}
	
	public boolean isValid() {
		//if less than 3 cols , we assume not regular record row, skip
		return isValid;
	}
	
	
	public void build(String[] nextRecord) {
		//if less than 3 cols , we assume not regular record row, skip
		if (nextRecord.length < 3) return;
		
		try {
		this.loan_amnt =  Double.parseDouble(nextRecord[Index.loan_amnt.id].trim());
		String rate_str= nextRecord[Index.int_rate.id].trim();
		rate_str= rate_str.substring(0,rate_str.length()-1);	
		this.int_rate = Double.parseDouble(rate_str)/100.0;
		
		this.rate_weighted_accu = this.int_rate * this.loan_amnt;
		
		this.grade = nextRecord[Index.grade.id].trim();
		if(nextRecord[Index.grade.id].equalsIgnoreCase("F") || nextRecord[Index.grade.id].equalsIgnoreCase("G")) {
			this.grade="FG";
		}
		
		this.loan_status = nextRecord[Index.loan_status.id].trim();
		
		this.out_prncp = Double.parseDouble(nextRecord[Index.out_prncp.id].trim());
		this.total_rec_prncp = Double.parseDouble(nextRecord[Index.total_rec_prncp.id].trim());
		this.total_rec_int = Double.parseDouble(nextRecord[Index.total_rec_int.id].trim());
		this.recoveries = Double.parseDouble(nextRecord[Index.recoveries.id].trim());
		
		}catch(Exception e) {
			System.out.println(e);
			return ;
		}
		
		isValid=true;

	}
	

}
