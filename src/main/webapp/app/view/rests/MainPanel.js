Ext.define("TR.view.rests.MainPanel", {
    extend : "Ext.panel.Panel",
    border: false,
    layout: 'fit',
    title : 'ნაშთები',
    constructor : function(cfg) {
        cfg = cfg || {};
        var me = this;
        
        var panel = Ext.create('Ext.panel.Panel',{
        });
        
        me.items = [ panel ];
        
        me.callParent(arguments);
        
        myRequest({
            url : 'rest/transaction/rests/currencies',
            method : 'GET',
            callback : function(data){
                console.log(data);
            }
        });
    }
});