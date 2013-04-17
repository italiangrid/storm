package it.grid.storm.asynch;

import it.grid.storm.asynch.Copy.Result;
import it.grid.storm.asynch.Copy.ResultType;
import it.grid.storm.srm.types.TRequestToken;
import it.grid.storm.srm.types.TReturnStatus;
import it.grid.storm.srm.types.TSizeInBytes;
import it.grid.storm.srm.types.TTURL;
import java.util.List;

public class OperationResultBuilder {

	public static Result build(Copy copy, List<Object> arguments, ResultType type) {

		switch (type) {
		case GET:
			if (arguments.size() != 4) {
				throw new IllegalArgumentException(
					"Unable to create GetOperationResult, requierd 4 parameters, provides \'"
						+ arguments.size() + "\'");
			}
			if (arguments.get(1) != null
				&& !(arguments.get(1) instanceof TReturnStatus)) {
				throw new IllegalArgumentException(
					"Unable to create GetOperationResult, parameter 1 must be a TReturnStatus, provided \'"
						+ arguments.get(1).getClass() + "\'");
			}
			if (arguments.get(2) != null && !(arguments.get(2) instanceof TTURL)) {
				throw new IllegalArgumentException(
					"Unable to create GetOperationResult, parameter 2 must be a TTURL, provided \'"
						+ arguments.get(2).getClass() + "\'");
			}
			if (arguments.get(3) != null
				&& !(arguments.get(3) instanceof TSizeInBytes)) {
				throw new IllegalArgumentException(
					"Unable to create GetOperationResult, parameter 3 must be a TSizeInBytes, provided \'"
						+ arguments.get(3).getClass() + "\'");
			}
			if (!(arguments.get(4) != null && arguments.get(4) instanceof TRequestToken)) {
				throw new IllegalArgumentException(
					"Unable to create GetOperationResult, parameter 4 must be a TRequestToken, provided \'"
						+ arguments.get(4).getClass() + "\'");
			}
			return copy.new GetOperationResult((TReturnStatus) arguments.get(1),
				(TTURL) arguments.get(2), (TSizeInBytes) arguments.get(3),
				(TRequestToken) arguments.get(4));
		case PUT:
			if (arguments.size() != 3) {
				throw new IllegalArgumentException(
					"Unable to create GetOperationResult, requierd 3 parameters, provides \'"
						+ arguments.size() + "\'");
			}
			if (arguments.get(1) != null
				&& !(arguments.get(1) instanceof TReturnStatus)) {
				throw new IllegalArgumentException(
					"Unable to create GetOperationResult, parameter 1 must be a TReturnStatus, provided \'"
						+ arguments.get(1).getClass() + "\'");
			}
			if (arguments.get(2) != null && !(arguments.get(2) instanceof TTURL)) {
				throw new IllegalArgumentException(
					"Unable to create GetOperationResult, parameter 2 must be a TTURL, provided \'"
						+ arguments.get(2).getClass() + "\'");
			}
			if (arguments.get(3) != null
				&& !(arguments.get(3) instanceof TRequestToken)) {
				throw new IllegalArgumentException(
					"Unable to create GetOperationResult, parameter 3 must be a TRequestToken, provided \'"
						+ arguments.get(3).getClass() + "\'");
			}
			return copy.new PutOperationResult((TReturnStatus) arguments.get(1),
				(TTURL) arguments.get(2), (TRequestToken) arguments.get(3));
		case TRANSFER:
			if (arguments.size() != 0) {
				throw new IllegalArgumentException(
					"Unable to create GetOperationResult, requierd 0 parameters, provides \'"
						+ arguments.size() + "\'");
			}
			return copy.new TransferResult();
		default:
			throw new IllegalArgumentException(
				"Unable to create a Result, unknown Copy.ResultType \'" + type + "\'");
		}
	}

	public static Result build(Copy copy, String string, ResultType type) {

		switch (type) {
		case GET:
			return copy.new GetOperationResult(string);
		case PUT:
			return copy.new PutOperationResult(string);
		case TRANSFER:
			return copy.new TransferResult(string);
		default:
			throw new IllegalArgumentException(
				"Unable to create a Result, unknown Copy.ResultType \'" + type + "\'");
		}
	}

}
