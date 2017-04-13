SELECT
  ORD.*,IFNULL(BOP.ORDER_QTY,0) BUY_QTY, IFNULL(INV.STOCK_QTY,0)STOCK_QTY
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
  LEFT JOIN (
              SELECT
                  sum(ORDER_QTY)ORDER_QTY,
                  PRODUCT
              FROM buy_order_product  GROUP BY PRODUCT

            ) BOP ON BOP.PRODUCT = ORD.PRODUCT
WHERE
  sale_order_no =:ORDER_NO