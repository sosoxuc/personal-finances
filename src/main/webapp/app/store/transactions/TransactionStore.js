Ext.define("TR.store.transactions.TransactionStore", {
    extend : "Ext.data.BufferedStore",
    storeId : 'transactionsStore',
    buffered : true,
    leadingBufferZone : 50,
    pageSize : 50,
    autoLoad : true,
    fields : [
        'id',
        'transactionAmount',
        'transactionRests',
        {
            name: 'transactionRestValue',
            calculate: function (data) {
                var rests = data.transactionRests;
                var result = null;
                Ext.Array.forEach(rests, function(rest){
                    if (rest.transactionRestType=='CURRENCY'){
                        result = rest.transactionRest;
                    }
                });
                return result;
            }
        },
        'transactionOrder',
        'transactionDate',
        'accountId',
        'accountName',
        'currencyId',
        'currencyCode',
        {
            name : 'userDate',
            convert : function(v) {
                return v ? new Date(v) : '';
            }
        },
        'note',
        'employeeFullName' ],
    proxy : {
        url : 'rest/transaction/search',
        type : 'rest',
        reader : {
            type : 'json',
            rootProperty : 'list',
            totalProperty : 'count'
        },
        actionMethods: {
            read: 'POST'
        }
    }
});