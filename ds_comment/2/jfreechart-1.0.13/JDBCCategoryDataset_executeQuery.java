/**
     * Populates the dataset by executing the supplied query against the
     * existing database connection.  If no connection exists then no action
     * is taken.
     * <p>
     * The results from the query are extracted and cached locally, thus
     * applying an upper limit on how many rows can be retrieved successfully.
     *
     * @param con  the connection.
     * @param query  the query.
     *
     * @throws SQLException if there is a problem executing the query.
     */
    public void executeQuery(Connection con, String query) throws SQLException {

        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = con.createStatement();
            resultSet = statement.executeQuery(query);
            ResultSetMetaData metaData = resultSet.getMetaData();

            int columnCount = metaData.getColumnCount();
            int columnCount = metaData.getColumnCount();

            if (columnCount < 2) {
                throw new SQLException(
                    "JDBCCategoryDataset.executeQuery() : insufficient columns "
                    + "returned from the database.");
            }

            // Remove any previous old data
            int i = getRowCount();
            while (--i >= 0) {
                removeRow(i);
            }

            while (resultSet.next()) {
                // first column contains the row key...
                Comparable rowKey = resultSet.getString(1);
                for (int column = 2; column <= columnCount; column++) {

                    Comparable columnKey = metaData.getColumnName(column);
                    int columnType = metaData.getColumnType(column);

                    switch (columnType) {
                        case Types.TINYINT:
                        case Types.SMALLINT:
                        case Types.INTEGER:
                        case Types.BIGINT:
                        case Types.FLOAT:
                        case Types.DOUBLE:
                        case Types.DECIMAL:
                        case Types.NUMERIC:
                        case Types.REAL: {
                            Number value = (Number) resultSet.getObject(column);
                            if (this.transpose) {
                                setValue(value, columnKey, rowKey);
                            }
                            else {
                                setValue(value, rowKey, columnKey);
                            }
                            break;
                        }
                        case Types.DATE:
                        case Types.TIME:
                        case Types.TIMESTAMP: {
                            Date date = (Date) resultSet.getObject(column);
                            Number value = new Long(date.getTime());
                            if (this.transpose) {
                                setValue(value, columnKey, rowKey);
                            }
                            else {
                                setValue(value, rowKey, columnKey);
                            }
                            break;
                        }
                        case Types.CHAR:
                        case Types.VARCHAR:
                        case Types.LONGVARCHAR: {
                            String string
                                = (String) resultSet.getObject(column);
                            try {
                                Number value = Double.valueOf(string);
                                if (this.transpose) {
                                    setValue(value, columnKey, rowKey);
                                }
                                else {
                                    setValue(value, rowKey, columnKey);
                                }
                            }
                            catch (NumberFormatException e) {
                                // suppress (value defaults to null)
                            }
                            break;
                        }
                        default:
                            // not a value, can't use it (defaults to null)
                            break;
                    }
                }
            }

            fireDatasetChanged();
        }
        finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                }
                catch (Exception e) {
                    // report this?
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                }
                catch (Exception e) {
                    // report this?
                }
            }
        }
    }