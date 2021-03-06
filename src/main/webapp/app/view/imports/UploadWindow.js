Ext.define('TR.view.imports.UploadWindow', {
	extend : 'Ext.window.Window',
	modal : true,
	width : 500,
	autoShow : true,
	title : LANG.UPLOAD_FILE,
	layout : 'fit',
	constructor : function(cfg) {
		cfg = cfg || {};
		var me = this;
		
		var projectsStore = Ext.StoreManager.lookup('projectsStore') || Ext.create('TR.store.projects.Store');
        var accountsStore = Ext.StoreManager.lookup('accountsStore') || Ext.create('TR.store.accounts.Store');

        var accountsCombo = Ext.create('Ext.form.field.ComboBox', {
            name: 'accountId',
            fieldLabel : LANG.ACCOUNT,
            queryMode: 'local',
            store: accountsStore,
            displayField: 'accountName',
            valueField: 'id',
            value: cfg.data ? cfg.data.accountId : ''
        });
        
        var projectsCombo = Ext.create('Ext.form.field.ComboBox', {
            name: 'projectId',
            queryMode: 'local',
            store: projectsStore,
            displayField: 'projectName',
            fieldLabel : LANG.PROJECT,
            valueField: 'id',
            value: cfg.data ? cfg.data.projectId : ''
        });
        
		var form = Ext.create('Ext.form.Panel', {
			border : false,
			bodyPadding : 5,
			fieldDefaults : {
				labelAlign : 'right',
				labelWidth : 130,
				anchor : '100%',
				allowBlank : false
			},
			items : [ {
				xtype : 'filefield',
				fieldLabel : LANG.FILE,
				name : 'file',
				buttonText : LANG.CHOOSE,
				isEng : true
			}, accountsCombo, projectsCombo ]
		});

		me.items = [ form ];

		me.buttons = [ {
			text : LANG.UPLOAD,
			handler : cfg.edit ? edit : save
		}, {
			text : LANG.CANCEL,
			handler : close
		} ];

		me.callParent(arguments);

		function save() {
			if (!form.getForm().isValid()) return;
			
			form.submit({
				url : 'rest/transaction/upload',
				waitMsg : LANG.UPLOADING,
				success : function(fp, o) {
					close();
					Ext.Msg.alert(LANG.STATUS, LANG.FILE_UPLOADED);
				},
				failure : function(fp, o) {
					var errTxt = drawErrorText(o.result);
					
					var errWin = {};
					errWin = Ext.create('Ext.window.Window', {
						modal: true,
						autoShow: true,
						bodyPadding: 5,
						html: errTxt,
						title: LANG.ERROR,
						maxWidth: 600,
						maxHeight: 600,
						autoScroll: true,
						buttonAlign: 'center',
						buttons: [{
							text: 'Ok',
							scope: this,
							handler: function(){
								errWin.close();
							}
						}]
						
					});
				}
			});
		}

		function edit() {
			if (!form.getForm().isValid())
				return;
			var values = form.getForm().getValues();
			myRequest({
				params : values,
				url : '../rest/test/updateFile',
				callback : function(response) {
					cfg.fileGrid.store.load();
					close();
				}
			});
		}
		
		function drawErrorText(result){
			var errTxt = '<strong>' + (result.error || '') + '</strong>';
			var errs = result.errors;
			errTxt += '<ol style="line-height: 20px">';
			for(var i in errs){
				errTxt += '<li><strong>შეცდომა ' + errs[i].number + ' ხაზზე: </strong>' 
					+ errs[i].message;
				errTxt += '<br /><div style="background:#fdd;padding:2px;margin:7px">' 
					+ errs[i].line + '</div></li>';
			}
			errTxt += '</ol>';
			return errTxt;
		}

		function close() {
			me.close();
		}
	}
});