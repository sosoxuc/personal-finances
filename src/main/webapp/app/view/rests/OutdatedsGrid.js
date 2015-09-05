Ext.define("TR.view.rests.OutdatedsGrid", {
    extend : "Ext.grid.Panel",
    border : false,
    viewConfig: { 
        stripeRows: false, 
        getRowClass: function(record) { 
            if (record.get('transactionType')===1) {
                return record.get('transactionAmount') > 0 ? 'income-row-planned' : 'outcome-row-planned';
            } else {
                return record.get('transactionAmount') > 0 ? 'income-row' : 'outcome-row'; 
            }
        }
    }, 
    constructor : function(cfg) {
        cfg = cfg || {};
        var me = this;

        me.store = Ext.create('TR.store.transactions.OutdatedsStore');

        me.columns = [ {
            xtype : 'datecolumn',
            header : LANG.DATE,
            dataIndex : 'transactionDate',
            flex : 1,
            format : 'd-m-Y',
            align : 'right'
        }, {
            header : LANG.PROJECT,
            dataIndex : 'projectName',
            flex : 1.5,
            align : 'right'
        }, {
            header : LANG.DESTINATION,
            dataIndex : 'transactionNote',
            flex : 2.5
        }, {
            header : LANG.SUM,
            xtype : 'numbercolumn',
            format : '0.00',
            align : 'right',
            dataIndex : 'transactionAmount',
            flex : 1,
            cls: 'bold',
            tdCls: 'bold'
        }, {
            header : LANG.CURRENCY,
            dataIndex : 'currencyCode',
            flex : 1,
            cls: 'bold',
            tdCls: 'bold'
        }, {
            header : LANG.REST,
            xtype : 'numbercolumn',
            format : '0.00',
            align : 'right',
            dataIndex : 'transactionRestValue',
            flex : 1
        }, {
            header : LANG.CURRENCY,
            dataIndex : 'currencyCode',
            flex : 1
        }, {
            header : LANG.ACCOUNT,
            dataIndex : 'accountName',
            flex : 1
        }, {
            header : LANG.USER,
            dataIndex : 'employeeFullName',
            flex : 1
        }, {
            xtype : 'actioncolumn',
            header : LANG.ACTION,
            width : 100,
            align : 'center',
            items : [{
                iconCls: 'x-tool-img x-tool-plus action-icon',
                tooltip : 'Accept',
                handler : function (grid, rowIndex, colIndex, item, e, record) {
                    approve(record.data.id, record.data.version);
                },
                scope : me
            }, {
                iconCls: 'x-tool-img x-tool-refresh action-icon',
                tooltip : 'Edit',
                handler : function (grid, rowIndex, colIndex, item, e, record) {
                    edit(record.data);
                },
                scope : me
            }, {
                iconCls : 'x-tool-img x-tool-close action-icon',
                tooltip : 'Remove',
                handler : function (grid, rowIndex, colIndex, item, e, record) {
                    remove(record.data.id, record.data.version);
                },
                scope : me
            } ]
        } ];

        me.callParent(arguments);

        function approve(id, version) {
            
            myRequest({
                url : 'rest/transaction/approve',
                params : {
                    transactionId: id,
                    version : version
                },
                callback : function(res) {
                    Ext.toast(LANG.SUCCESSED);
                    me.store.load();
                    me.getSelectionModel().deselectAll();
                }
            });
        }
        
        function edit(data) {
            Ext.create('TR.view.transactions.AddWindow', {
                grid : me,
                edit : true,
                data: data
            }).show();
        }

        function remove(id, version) {
            Ext.Msg.confirm(LANG.CONFIRM, LANG.CONFIRM_REMOVAL, function(ans) {
                if (ans === 'yes') {
                    myRequest({
                        url : 'rest/transaction/remove',
                        params : {
                            id : id,
                            version : version
                        },
                        callback : function(res) {
                            me.store.load();
                            me.getSelectionModel().deselectAll();
                        }
                    });
                }
            });
        }
    }
});
