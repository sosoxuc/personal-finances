package personal.finances.transactions;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "TRANSACTIONS")
public class Transaction implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "Transaction")
	@TableGenerator(name = "Transaction")
	private Integer id;

	@Temporal(TemporalType.DATE)
	private Date transactionDate;

	private Integer employeeId;
	
	private Integer employeeName;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date userDate;

	private BigDecimal transactionAmount;
	
	private Integer debitAccountId;
	
	private String debitAccountName;
	
	private Integer creditAccountId;
	
	private String creditAccountName;

	private Integer projectId;
	
	private String projectName;

	private Integer reasonId;
	
	private String reasonName;
	
	private Integer transactionState;
	
	@Lob
	private String note;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public Integer getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Integer employeeId) {
		this.employeeId = employeeId;
	}

	public Integer getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(Integer employeeName) {
		this.employeeName = employeeName;
	}

	public Date getUserDate() {
		return userDate;
	}

	public void setUserDate(Date userDate) {
		this.userDate = userDate;
	}

	public BigDecimal getTransactionAmount() {
		return transactionAmount;
	}

	public void setTransactionAmount(BigDecimal transactionAmount) {
		this.transactionAmount = transactionAmount;
	}

	public Integer getDebitAccountId() {
		return debitAccountId;
	}

	public void setDebitAccountId(Integer debitAccountId) {
		this.debitAccountId = debitAccountId;
	}

	public String getDebitAccountName() {
		return debitAccountName;
	}

	public void setDebitAccountName(String debitAccountName) {
		this.debitAccountName = debitAccountName;
	}

	public Integer getCreditAccountId() {
		return creditAccountId;
	}

	public void setCreditAccountId(Integer creditAccountId) {
		this.creditAccountId = creditAccountId;
	}

	public String getCreditAccountName() {
		return creditAccountName;
	}

	public void setCreditAccountName(String creditAccountName) {
		this.creditAccountName = creditAccountName;
	}

	public Integer getProjectId() {
		return projectId;
	}

	public void setProjectId(Integer projectId) {
		this.projectId = projectId;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Integer getReasonId() {
		return reasonId;
	}

	public void setReasonId(Integer reasonId) {
		this.reasonId = reasonId;
	}

	public String getReasonName() {
		return reasonName;
	}

	public void setReasonName(String reasonName) {
		this.reasonName = reasonName;
	}

	public Integer getTransactionState() {
		return transactionState;
	}

	public void setTransactionState(Integer transactionState) {
		this.transactionState = transactionState;
	}
}
