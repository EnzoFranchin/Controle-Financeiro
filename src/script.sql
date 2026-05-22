CREATE TABLE categorias (
                            id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                            nome VARCHAR2(50) NOT NULL,
                            tipo VARCHAR2(10) CHECK (tipo IN ('RECEITA', 'DESPESA')) NOT NULL
);

CREATE TABLE transacoes (
                            id NUMBER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                            descricao VARCHAR2(100) NOT NULL,
                            valor NUMBER(10, 2) NOT NULL,
                            data_transacao DATE DEFAULT SYSDATE NOT NULL,
                            categoria_id NUMBER,
                            CONSTRAINT fk_categoria FOREIGN KEY (categoria_id) REFERENCES categorias(id)
);