Ext.define("TR.view.transactions.TransactionGrid", {
    extend : "Ext.grid.Panel",
    border : false,
    viewConfig: { 
        stripeRows: false, 
        getRowClass: function(record) { 
            return record.get('transactionAmount') > 0 ? 'income-row' : 'outcome-row'; 
        } 
    }, 
    constructor : function(cfg) {
        cfg = cfg || {};
        var me = this;

        me.store = Ext.create('TR.store.transactions.TransactionStore');

        me.tbar = [ {
            text : LANG.ADD,
            name : 'add',
            menu: [{
                text: LANG.INCOME,
                handler : addIncome
            }, {
                text: LANG.EXPENCE,
                handler : addExpence
            }, '-', {
                text: LANG.TRANSACTION,
                handler : add
            },'-', {
                text: LANG.MOVE_PROJECT,
                handler : moveProject
            }, {
                text: LANG.MOVE_CURRENCY,
                handler : moveCurrency
            }, {
                text: LANG.MOVE_ACCOUNT,
                handler : moveAccount
            }]
        }, {
            text : LANG.EDIT,
            name : 'edit',
            disabled: true,
            handler : edit
        }, {
            text : LANG.REMOVE,
            name : 'remove',
            disabled: true,
            handler : remove
        },'-',{
            text : LANG.UP,
            handler : up,
            disabled: true,
            name : 'up'
        },{
            text : LANG.DOWN,
            handler : down,
            disabled: true,
            name : 'down'
        },'-', {
            text : LANG.CALCULATE,
            name : 'calcualte',
            handler : calcualte
        }, '-', {
            text : LANG.EXPORT,
            href : '#',
            name : 'export',
            hrefTarget: '_blank'
        }];

        me.load = function(params) {
            me.store.getProxy().extraParams = params;
            me.store.load();
        }

        me.columns = [ {
            xtype : 'datecolumn',
            header : LANG.DATE,
            dataIndex : 'transactionDate',
            flex : 1,
            format : 'd-m-Y',
            align : 'right'
        }, {
            header : LANG.SUM,
            xtype : 'numbercolumn',
            format : '0.00',
            align : 'right',
            dataIndex : 'transactionAmount',
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
            header : LANG.PROJECT,
            dataIndex : 'projectName',
            flex : 1.5
        }, {
            header : LANG.DESTINATION,
            dataIndex : 'transactionNote',
            flex : 2.5
        } ];

        me.callParent(arguments);

        me.on('select', function(view, rec){
            me.down('button[name=edit]').enable();
            me.down('button[name=remove]').enable();
            me.down('button[name=down]').enable();
            me.down('button[name=up]').enable();
        });

        me.exporthref = exporthref;
        
        function exporthref(values) {
            me.down('button[name=export]').url = 'rest/transaction/export/excel?'+serialize(values);
        }
        
        function calcualte() {
            myRequest({
                url : 'rest/transaction/rests/calculate',
                callback : function(res) {
                    me.store.load();
                }
            });
        }
        
        function moveCurrency() {
            Ext.create('TR.view.operations.CurrencyWindow',{
                grid : me,
                searchForm : me.searchForm
            }).show();
        }
        
        function moveAccount() {
            Ext.create('TR.view.operations.AccountWindow',{
                grid : me,
                searchForm : me.searchForm
            }).show();
        }
        
        function moveProject() {
            Ext.create('TR.view.operations.ProjectWindow',{
                grid : me,
                searchForm : me.searchForm
            }).show();
        }
        
        function addIncome() {
            Ext.create('TR.view.transactions.AddWindow', {
                grid : me,
                searchForm : me.searchForm,
                direction: 1
            }).show();
        }
        
        function addExpence() {
            Ext.create('TR.view.transactions.AddWindow', {
                grid : me,
                searchForm : me.searchForm,
                direction: -1
            }).show();
        }
        
        function add() {
            Ext.create('TR.view.transactions.AddWindow', {
                grid : me,
                searchForm : me.searchForm
            }).show();
        }

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
        
        function down(btn) {
            var sel = me.getSelectionModel().getSelection();
            if(!sel.length) return ;
            var rec = sel[0];
            myRequest({
                params : {
                    transactionId : rec.get('id'),
                    direction: -1
                },
                url : 'rest/transaction/shift',
                callback : function(response) {
                    me.store.load();
                }
            });
        }
        
        function up() {
            var sel = me.getSelectionModel().getSelection();
            if(!sel.length) return ;
            var rec = sel[0];
            myRequest({
                params : {
                    transactionId : rec.get('id'),
                    direction: 1
                },
                url : 'rest/transaction/shift',
                callback : function(response) {
                    me.store.load();
                }
            });
        }
        
    }
});
