package compiler488.ast.decl;

/**
 * Holds the declaration part of an array.
 */
public class ArrayDeclPart extends DeclarationPart {

	/* The lower and upper boundaries of the array. */
        private Integer lb1, ub1, lb2, ub2;
	private Boolean isTwoDimensional = false ;


	/* The number of objects the array holds. */
	private Integer size;

	/**
	 * Returns a string that describes the array.
	 */
	@Override
	public String toString() {
		return name + "[" + lb1 + ".." + ub1 +
		( isTwoDimensional ?  "," + lb2 + ".." + ub2 : "" )
		+ "]";
	}

	public Integer getSize() {
		return size;
	}


	public Integer getLowerBoundary1() {
		return lb1;
	}

	public Integer getUpperBoundary1() {
		return ub1;
	}

        public void setLowerBoundary1(Integer lb1) {
		this.lb1 = lb1;
	}

        public void setUpperBoundary1(Integer ub1) {
		this.ub1 = ub1;
	}

	public Integer getLowerBoundary2() {
		assert isTwoDimensional ;	// check for misuse
		return lb2;
	}

	public Integer getUpperBoundary2() {
		assert isTwoDimensional ;       // check for misuse
		return ub2;
	}

        public void setLowerBoundary2(Integer lb2) {
                this.isTwoDimensional = true ;
		this.lb2 = lb2;
	}

        public void setUpperBoundary2(Integer ub2) {
	        this.isTwoDimensional = true ;
		this.ub2 = ub2 ;
	}

	public void setSize(Integer size) {
		this.size = size;
	}
}
