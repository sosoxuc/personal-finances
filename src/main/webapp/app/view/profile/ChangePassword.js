Ext.define("TR.view.profile.ChangePassword", {
    extend : "Ext.window.Window",
    modal : true,
    width : 350,
    autoHeight: true,
    constructor : function(cfg) {
        cfg = cfg || {};
        var me = this;
        
        var form = Ext.create('Ext.form.Panel', {
            border : false,
            bodyPadding : 5,
            name : 'form',
            fieldDefaults : {
                labelWidth : 150,
                labelAlign : 'right',
                anchor : '100%',
                allowBlank : false,
                inputType : 'password'
            },
            defaultType : 'textfield',
            items : [{
                name: 'oldPass',
                fieldLabel : LANG.OLD_PASSWORD,
                isEng : true
            },{
                name: 'newPass',
                fieldLabel : LANG.PASSWORD,
                isEng : true
            },{
                name: 'newPass2',
                fieldLabel : LANG.PASSWORD2,
                isEng : true
            }]
        });

        me.items = [ form ];
        
        me.buttons = [{
            text: LANG.CHANGE_PASSWORD,
            handler: changePassword
        }];
        
        me.callParent(arguments);
        
        function changePassword(){
            if (!form.getForm().isValid())
                return;
            
            var values = form.getForm().getValues();
            
            if (values.newPass != values.newPass2) {
                Ext.toast(LANG.PASSWORDS_DONT_MATCH);
                return;
            }
            
            myRequest({
                url : 'rest/security/password/change',
                params : values,
                callback : function() {
                    me.close();
                    Ext.toast(LANG.SUCCESSED);
                },
                error : function(response) {
                    Ext.toast(LANG.INVALID_PASSWORD);
                }
            });
        }
    }
});