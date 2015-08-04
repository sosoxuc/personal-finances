Ext.define("TR.view.rests.MainPanel", {
    extend : "Ext.panel.Panel",
    border: false,
    title: LANG.RESTS,
    layout : {
        type : 'vbox',
        align : 'stretch'
    },
    constructor : function(cfg) {
        cfg = cfg || {};
        var me = this;
        
        var panel = Ext.create('Ext.panel.Panel',{
            defaultType : 'textfield',
            id: 'rests-panel',
            title : LANG.CURRENT_RESTS,
            buttonAlign: 'left',
            buttons: [{
                text : LANG.REFRESH,
                handler : load
            }]
        });
        
        me.items = [ panel ];
        
        me.callParent(arguments);
        
        me.addListener({
            activate: load
        });
        
        function load() {
            panel.mask(LANG.LOADING);
            myRequest({
                url : 'rest/transaction/rests/currencies',
                method : 'GET',
                callback : function(data){
                    panel.removeAll();
                    Ext.Array.forEach(data, function(item){
                        panel.add({
                            fieldLabel : item.resourceName,
                            labelAlign : 'right',
                            labelWidth : 150,
                            value: item.transactionRest,
                            readOnly: true
                        });
                    });
                    panel.unmask();
                }
            });
        }
    }
});