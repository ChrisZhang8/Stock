SELECT
	ORD.*, IFNULL(INV.STOCK_QTY,0)STOCK_QTY
FROM
	SALE_ORDER_PRODUCT ORD
LEFT JOIN (
	SELECT
		sum(QTY) STOCK_QTY,
		PRODUCT
	FROM
		INVentory
	GROUP BY
		product
) INV ON ORD.PRODUCT = INV.PRODUCT
WHERE
	sale_order_no =:ORDER_NO