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
        } ];

        me.callParent(arguments);

        function edit() {
            var sel = me.getSelectionModel().getSelection();
            if (sel.length == 0)
                return;

            Ext.create('TR.view.transactions.AddWindow', {
                grid : me,
                edit : true,
                data: sel[0].getData()
            }).show();
        }

        function remove() {
            var sel = me.getSelectionModel().getSelection();
            if (sel.length == 0)
                return;
            Ext.Msg.confirm(LANG.CONFIRM, LANG.CONFIRM_REMOVAL, function(ans) {
                if (ans === 'yes') {
                    myRequest({
                        url : 'rest/transaction/remove',
                        params : {
                            id : sel[0].get('id'),
                            version : sel[0].get('version')
                        },
                        callback : function(res) {
                            me.store.load();
                            me.getSelectionModel().deselectAll();
                            me.down('button[name=edit]').disable();
                            me.down('button[name=remove]').disable();
                            me.down('button[name=down]').disable();
                            me.down('button[name=up]').disable();
                        }
                    });
                }
            });
        }
    }
});
