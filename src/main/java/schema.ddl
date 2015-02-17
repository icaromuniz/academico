
    create table PessoaFisica (
        id int4 not null,
        dataCriacao time not null,
        usuarioCriacao varchar(11) not null,
        versao int4 not null,
        bairro varchar(255),
        cpf varchar(11) not null unique,
        dddAlternativo varchar(2),
        dddFixo varchar(2),
        email varchar(255),
        endereço varchar(255),
        nascimento date,
        nome varchar(255) not null,
        telefoneAlternativo varchar(9),
        telefoneFixo varchar(8),
        primary key (id)
    );

    create table Usuario (
        id int4 not null,
        dataCriacao time not null,
        usuarioCriacao varchar(11) not null,
        versao int4 not null,
        administrador bool not null,
        ativo bool not null,
        senha varchar(10),
        pessoaFisica_id int4 not null,
        primary key (id)
    );

    alter table Usuario 
        add constraint FK5B4D8B0E9C886F0D 
        foreign key (pessoaFisica_id) 
        references PessoaFisica;

    create sequence SEQ_USUARIO;

    create sequence seq_pessoa_fisica;
