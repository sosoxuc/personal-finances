package personal.finances.transactions.rest;

/**
 * Created by niko on 7/27/15.
 */
public class RestDescriptor {

    public TransactionRestType transactionRestType;
    public Integer referenceId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RestDescriptor)) return false;

        RestDescriptor that = (RestDescriptor) o;

        if (referenceId != null ? !referenceId.equals(that.referenceId) : that.referenceId != null) return false;
        if (transactionRestType != that.transactionRestType) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = transactionRestType != null ? transactionRestType.hashCode() : 0;
        result = 31 * result + (referenceId != null ? referenceId.hashCode() : 0);
        return result;
    }
}
