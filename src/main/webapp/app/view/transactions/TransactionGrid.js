Ext.define("TR.view.transactions.TransactionGrid", {
    extend : "Ext.grid.Panel",
    border : false,
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
            handler : edit
        }, {
            text : 'წაშლა',
            name : 'remove',
            handler : remove
        } ];

        me.load = function(date1, date2) {
            me.store.getProxy().extraParams = {
                employee : cfg.employeeId,
                startDate : date1 || '',
                endDate : date2 || ''
            };
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
            header : 'პროექტი',
            dataIndex : 'projectName',
            flex : 1.5
        }, {
            header : 'დანიშნულება',
            dataIndex : 'reasonName',
            flex : 1
        }, {
            header : 'დასახელება',
            dataIndex : 'debitAccountName',
            flex : 2
        }, {
            header : 'კორესპოდენტი',
            dataIndex : 'creditAccountName',
            flex : 1.5
        }, {
            header : 'შენიშვნა',
            dataIndex : 'note',
            flex : 3
        } ];

        
        
        me.callParent(arguments);

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

            var win = Ext.create('TR.view.transactions.AddWindow', {
                grid : me,
                edit : true,
                employeeId : sel[0].get('id'),
                searchForm : me.searchForm
            });
            var values = sel[0].getData();

            values.userRole = values.userRole == 2 ? 1 : 0;

            win.down('form').getForm().setValues(values);
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
                            id : sel[0].get('id')
                        },
                        callback : function(res) {
                            me.store.load();
                            me.getSelectionModel().deselectAll();
                            //resetButtons();
                        }
                    });
                }
            });
        }
        
    }
});