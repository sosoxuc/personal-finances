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
            text : 'დამატება',
            name : 'add',
            handler : add
        }, {
            text : 'რედაქტირება',
            name : 'edit',
            disabled: true,
            handler : edit
        }, {
            text : 'წაშლა',
            name : 'remove',
            disabled: true,
            handler : remove
        },'-',{
            text : 'ზევით',
            handler : up,
            disabled: true,
            name : 'up'
        },{
            text : 'ქვევით',
            handler : down,
            disabled: true,
            name : 'down'
        },'-', {
            text : 'რეკალკულაცია',
            name : 'recalcualte',
            handler : recalcualte
        }, '-', {
            text : 'ექსპორტი',
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
            header : 'თარიღი',
            dataIndex : 'transactionDate',
            flex : 1,
            format : 'd-m-Y',
            align : 'right'
        }, {
            header : 'თანხა',
            xtype : 'numbercolumn',
            format : '0.00',
            align : 'right',
            dataIndex : 'transactionAmount',
            flex : 1
        }, {
            header : 'ვალუტა',
            dataIndex : 'currencyCode',
            flex : 1
        }, {
            header : 'ანგარიში',
            dataIndex : 'accountName',
            flex : 1
        }, {
            header : 'ნაშთი',
            xtype : 'numbercolumn',
            format : '0.00',
            align : 'right',
            dataIndex : 'transactionRestValue',
            flex : 1
        }, {
            header : 'ვალუტა',
            dataIndex : 'currencyCode',
            flex : 1
        }, {
            header : 'პროექტი',
            dataIndex : 'projectName',
            flex : 1.5
        }, {
            header : 'დანიშნულება',
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
        
        function recalcualte() {
            myRequest({
                url : 'rest/transaction/rests/calculate',
                callback : function(res) {
                    me.store.load();
                }
            });
        }
        
        function add() {
            Ext.create('TR.view.transactions.AddWindow', {
                grid : me,
                searchForm : me.searchForm
            });
        }

        function edit() {
            var sel = me.getSelectionModel().getSelection();
            if (sel.length == 0)
                return;

            Ext.create('TR.view.transactions.AddWindow', {
                grid : me,
                edit : true,
                data: sel[0].getData()
            });
        }

        function remove() {
            var sel = me.getSelectionModel().getSelection();
            if (sel.length == 0)
                return;
            Ext.Msg.confirm('სტატუსი', 'დაადასტურეთ წაშლა!', function(ans) {
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
