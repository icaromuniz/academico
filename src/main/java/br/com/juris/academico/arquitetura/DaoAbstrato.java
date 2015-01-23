package br.com.juris.academico.arquitetura;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import br.com.juris.academico.geral.EntidadeAbstrata;

public abstract class DaoAbstrato<T extends EntidadeAbstrata> {

	@PersistenceContext
    private EntityManager em;
	
	private Class<T> entityClass = null;
	
	public DaoAbstrato(Class<T> entityClass){
		this.entityClass = entityClass;
	}

	public T save(T entidade){
		return em.merge(entidade);
	}
	
	public void remove(T entidade){
		em.remove(entidade);
	}
	
	public T find(Integer idEntidade){
		return em.find(entityClass, idEntidade);
	}
	
	public EntityManager getEm(){
		return em;
	}
}
