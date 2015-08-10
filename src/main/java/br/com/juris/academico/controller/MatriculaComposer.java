package br.com.juris.academico.controller;

import java.util.Date;
import java.util.List;

import javax.ejb.EJBTransactionRolledbackException;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;

import br.com.juris.academico.arquitetura.AbstractComposer;
import br.com.juris.academico.model.Matricula;
import br.com.juris.academico.model.PessoaFisica;
import br.com.juris.academico.model.Turma;
import br.com.juris.academico.model.Usuario;
import br.com.juris.academico.persistence.MatriculaDao;
import br.com.juris.academico.persistence.PessoaFisicaDao;
import br.com.juris.academico.persistence.TurmaDao;
import br.com.juris.academico.service.Util;

public class MatriculaComposer extends AbstractComposer<Matricula> {

	private static final long serialVersionUID = -8078166359065772931L;
	
	private EventListener<Event> listenerExclusao = new EventListener<Event>() {

		@Override
		public void onEvent(Event event) throws Exception {
			if (event.getName().equals("onOK")) {
				cancelaMatricula(getModelo());
			}
		}
	};
	
	// componentes do form
	private Combobox comboboxPessoaFisica;
	
	// componentes do list
	private Textbox filtroNome;
	private Combobox filtroUnidade;
	private Combobox filtroTurma;
	private Combobox filtroFormaPagamento;

	public MatriculaComposer() {
		super(Matricula.class);
	}
	
	@Override
	public void doAfterCompose(Component comp) throws Exception {
		
		super.doAfterCompose(comp);
		
		// desabilita edição da matrícula
		if (Executions.getCurrent().getDesktop().getRequestPath().contains("/form.zul")) {
			atribuiPermissaoEdicao(comboboxPessoaFisica.getParent().getParent(), getModelo().getId() == null);
		}
	}
	
	@Override
	public void salvaRegistro() {
		
		try {
			
			super.salvaRegistro();
			
		} catch (EJBTransactionRolledbackException e) {
			
			// alerta se o aluno já estiver matriculado na turma selecionada
			if (e.getCausedByException().getCause().getMessage().contains("un_pessoa_turma")) {
				Messagebox.show("O Aluno informado já está matriculado \nna Turma selecionada.",
						"Matrícula duplicada!", 1, Messagebox.EXCLAMATION);
				return;
			}
			
			throw e;
		}
		
		atribuiPermissaoEdicao(comboboxPessoaFisica.getParent().getParent(), false);
	}
	
	@Override
	public void excluiRegistro() {
		Messagebox.show("Confirma o cancelamento?", "Cancelamento de Matrícula", 3, Messagebox.EXCLAMATION, listenerExclusao);
	}

	@Override
	public void limpaFiltro() {
		filtroNome.setValue(null);
		filtroUnidade.setSelectedIndex(-1);
		((ListModelList<?>)filtroTurma.getModel()).clearSelection();
		filtroFormaPagamento.setSelectedIndex(-1);
	}

	@Override
	public void filtraLista() {
		setListaModelo(((MatriculaDao)dao).findByFiltro(filtroNome.getValue(), 
				filtroUnidade.getSelectedItem() != null ? filtroUnidade.getSelectedItem().getLabel() : null,
				filtroTurma.getSelectedItem() != null ? ((Turma)filtroTurma.getSelectedItem().getValue()).getId() : null,
				filtroFormaPagamento.getSelectedItem() != null ? filtroFormaPagamento.getSelectedItem().getLabel() : null));
		getBinder().notifyChange(this, "*");
	}
	
	public void emiteContrato(){
		System.out.println("contrato");
	}

	private void cancelaMatricula(Matricula matricula) {
		
		matricula.setMatriculaAtiva(false);
		matricula.setUsuarioCancelamento((Usuario) Executions.getCurrent().getSession().getAttribute("usuario"));
		matricula.setDataCancelamento(new Date());
		
		super.salvaRegistro();
	}

	public List<PessoaFisica> getListaPessoaFisica(){
		PessoaFisicaDao pessoaFisicaDao = Util.getDao(PessoaFisicaDao.class);
		return pessoaFisicaDao.findByFiltro(null, null, null);
	}
	
	public List<Turma> getListaTurma(){
		Boolean somenteAtiva = getModelo() != null && getModelo().getId() == null ? true : null;
		TurmaDao turmaDao = Util.getDao(TurmaDao.class);
		return turmaDao.findByDisponibilidade( somenteAtiva );
	}
	
	public boolean isContratoDesabilitado(){
		
		// TODO (icaromuniz) Implementar verificação de 'Turma encerrada' e 'Matrícula Cancelada'
		
		return false;
	}
}
