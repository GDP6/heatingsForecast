public class Heating
	{
		Long start;
		Long end;
		double[] temps;

		

		public Heating(long start, long end, double[] temps) {
			this.start = start;
			this.end = end;
			this.temps = temps;
		}

		@Override
		public boolean equals(Object obj) {
			if(obj instanceof Heating)
			{
				
				Heating h = (Heating)obj;
				if(h.start == start && h.end == end)
				{
					return true;
				}
			}
			return false;
		}
		
		public void createCooling()
		{
			
		}
		
		


	}
