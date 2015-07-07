Ext.define("TR.store.transactions.TransactionStore", {
    extend : "Ext.data.Store",
    storeId : 'transactionsStore',
    leadingBufferZone : 150,
    sortable : false,
    defaultSortable : false,
    buffered : true,
    remoteSort : false,
    pageSize : 50,
    fields : [
        'id',
        'transactionAmount',
        'transactionRest', 
        'transactionOrder',
        {
            name : 'transactionDate',
            convert : function(v) {
                return v ? new Date(v) : '';
            }
        }, {
            name : 'userDate',
            convert : function(v) {
                return v ? new Date(v) : '';
            }
        },
        'note' ],
    proxy : {
        url : 'rest/transaction/search',
        type : 'rest',
        reader : {
            type : 'json'
        }
    }
});