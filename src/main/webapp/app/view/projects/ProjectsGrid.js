Ext.define("TR.view.projects.ProjectsGrid", {
    extend : "Ext.grid.Panel",
    border : false,
    constructor : function(cfg) {
        cfg = cfg || {};
        var me = this;
        me.store = Ext.StoreManager.lookup('projectsStore') || Ext.create('TR.store.projects.Store');
        
        me.tbar = [ {
            text : LANG.ADD,
            name : 'add',
            handler : add
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
        } ];

        me.columns = [ {
            header : LANG.NAME,
            dataIndex : 'projectName',
            flex : 1
        } ];

        me.callParent(arguments);
        
        me.on('select', function(view, rec){
            me.down('button[name=edit]').enable();
            me.down('button[name=remove]').enable();
        });

        function add() {
            Ext.create('TR.view.projects.AddWindow', {
                grid : me
            });
        }
        
        function edit() {
            var sel = me.getSelectionModel().getSelection();
            if (sel.length == 0)
                return;

            var addWindow = Ext.create('TR.view.projects.AddWindow', {
                grid : me,
                edit : true,
                data : sel[0].getData()
            });
        }

        function remove() {
            var sel = me.getSelectionModel().getSelection();
            if (sel.length == 0)
                return;
            Ext.Msg.confirm(LANG.CONFIRM, LANG.CONFIRM_REMOVAL, function(ans) {
                if (ans === 'yes') {
                    var rec = sel[0];
                    myRequest({
                        url : 'rest/project/remove',
                        params : {
                            id : rec.get('id'),
                            version: rec.get('version')
                        },
                        callback : function(res) {
                            me.store.load();
                            me.getSelectionModel().deselectAll();
                            me.down('button[name=edit]').disable();
                            me.down('button[name=remove]').disable();
                        }
                    });
                }
            });
        }

    }
});