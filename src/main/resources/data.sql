
INSERT INTO clientes (nome, email, telefone, endereco, ativo, data_criacao) VALUES
('Bruce Wayne', 'batman@wayneenterprises.com', '11999000001', 'Wayne Manor, 1007 - Gotham City/NY', true, CURRENT_TIMESTAMP),
('Tony Stark', 'ironman@starkindustries.com', '11999000002', 'Stark Tower, 200 - Malibu/CA', true, CURRENT_TIMESTAMP),
('Peter Parker', 'spiderman@dailybugle.com', '11999000003', 'Queens Avenue, 20 - New York/NY', true, CURRENT_TIMESTAMP),
('Diana Prince', 'wonderwoman@themyscira.com', '11999000004', 'Paradise Island, 1 - Washington/DC', true, CURRENT_TIMESTAMP),
('Clark Kent', 'superman@dailyplanet.com', '11999000005', 'Smallville Farm, 344 - Metropolis/KS', false, CURRENT_TIMESTAMP);

INSERT INTO restaurantes (nome, categoria, endereco, avaliacao, ativo, data_criacao) VALUES
('McDonalds da Times Square', 'Fast Food', 'Times Square, 1560 - New York/NY', 4.2, true, CURRENT_TIMESTAMP),
('KFC do Colonel Sanders', 'Frango Frito', 'Kentucky Avenue, 1890 - Louisville/KY', 4.5, true, CURRENT_TIMESTAMP),
('Subway da 5th Avenue', 'Sanduíches', '5th Avenue, 350 - New York/NY', 4.1, true, CURRENT_TIMESTAMP),
('Pizza Hut da Hollywood', 'Pizza', 'Hollywood Boulevard, 6801 - Los Angeles/CA', 4.3, true, CURRENT_TIMESTAMP),
('Burger King do Vegas', 'Hambúrgueres', 'Las Vegas Strip, 3667 - Las Vegas/NV', 4.0, true, CURRENT_TIMESTAMP),
('Taco Bell Demolition Man', 'Mexicana', 'Franchise Wars Street, 2032 - Future City/CA', 3.8, false, CURRENT_TIMESTAMP);


INSERT INTO produtos (nome, descricao, preco, categoria, disponivel, restaurante_id) VALUES
('Big Mac Especial', 'O clássico Big Mac com dois hambúrgueres e molho especial', 25.90, 'Hambúrguer', true, 1),
('McFlurry Oreo', 'Sorvete cremoso com pedaços de biscoito Oreo', 12.50, 'Sobremesa', true, 1),
('Batata Frita Grande', 'Batatas douradas e crocantes - porção grande', 8.90, 'Acompanhamento', true, 1),
('Chicken McNuggets 20un', 'Nuggets de frango crocantes - 20 unidades', 22.00, 'Frango', false, 1);


INSERT INTO produtos (nome, descricao, preco, categoria, disponivel, restaurante_id) VALUES
('Balde Família Original', 'Receita secreta do Colonel - 8 pedaços', 45.90, 'Balde', true, 2),
('Hot Wings Picantes', 'Asinhas super picantes com molho especial', 18.50, 'Frango', true, 2),
('Purê de Batata Cremoso', 'Purê tradicional com molho gravy', 7.00, 'Acompanhamento', true, 2),
('Torta de Maçã Kentucky', 'Torta caseira de maçã quentinha', 6.50, 'Sobremesa', true, 2);


INSERT INTO produtos (nome, descricao, preco, categoria, disponivel, restaurante_id) VALUES
('Sub Italian B.M.T. 30cm', 'Sanduíche com pepperoni, salame e presunto', 19.90, 'Sanduíche', true, 3),
('Cookie Chocolate Chip', 'Cookie artesanal com gotas de chocolate', 4.50, 'Sobremesa', true, 3),
('Wrap de Frango Teriyaki', 'Wrap com frango teriyaki e vegetais frescos', 16.90, 'Wrap', true, 3),
('Refrigerante Coca-Cola', 'Coca-Cola gelada 500ml', 5.90, 'Bebida', true, 3);


INSERT INTO produtos (nome, descricao, preco, categoria, disponivel, restaurante_id) VALUES
('Pizza Suprema Grande', 'Pizza com pepperoni, cogumelos, pimentão e azeitona', 42.90, 'Pizza', true, 4),
('Breadsticks com Alho', 'Pãezinhos com alho e queijo derretido', 15.00, 'Entrada', true, 4),
('Lasanha da Casa', 'Lasanha tradicional com molho bolonhesa', 28.50, 'Massa', true, 4);


INSERT INTO produtos (nome, descricao, preco, categoria, disponivel, restaurante_id) VALUES
('Whopper Duplo', 'Dois hambúrgueres grelhados com todos os ingredientes', 32.90, 'Hambúrguer', true, 5),
('Onion Rings Grande', 'Anéis de cebola crocantes e dourados', 9.50, 'Acompanhamento', true, 5),
('Sundae de Chocolate', 'Sorvete com calda de chocolate quente', 8.90, 'Sobremesa', true, 5);


INSERT INTO pedidos (cliente_id, restaurante_id, valor_total, status, data_pedido, observacoes) VALUES
(1, 1, 47.30, 'ENTREGUE', DATEADD('HOUR', -2, CURRENT_TIMESTAMP), 'Entregar na Batcaverna - entrada secreta'),
(2, 4, 57.90, 'ENTREGUE', DATEADD('HOUR', -1, CURRENT_TIMESTAMP), 'Tony Stark pediu extra queijo - conta da Stark Industries'),
(3, 3, 27.30, 'PREPARANDO', DATEADD('MINUTE', -30, CURRENT_TIMESTAMP), 'Peter Parker - sem cebola, sou alérgico!'),
(1, 2, 59.90, 'CONFIRMADO', DATEADD('MINUTE', -15, CURRENT_TIMESTAMP), 'Bruce Wayne - frango bem passado para o Alfred'),
(4, 1, 25.90, 'PENDENTE', DATEADD('MINUTE', -5, CURRENT_TIMESTAMP), 'Diana - entregar na embaixada de Themyscira'),
(2, 5, 51.30, 'PREPARANDO', DATEADD('MINUTE', -25, CURRENT_TIMESTAMP), 'Stark Tower - cobertura, heliponto H'),
(3, 3, 21.40, 'ENTREGUE', DATEADD('HOUR', -3, CURRENT_TIMESTAMP), 'Spider-Man - deixar na teia da esquina');
