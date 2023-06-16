package testiranje;

import logika.SetMapping;
import logika.UFDS;

public class UFDSTest {
	
	private static class NullMapping implements SetMapping {
		@Override
		public SetMapping joinWith(SetMapping other) {
			return other;
		}
	}
	
	public static void main(String[] args) {
		UFDS<String, NullMapping> uf = new UFDS<>();
		uf.insert("a", new NullMapping());
		uf.insert("b", new NullMapping());
		uf.insert("c", new NullMapping());
		uf.insert("d", new NullMapping());
		uf.insert("e", new NullMapping());
		uf.insert("f", new NullMapping());
		
		for (int i = 0; i < 6; i++) {
			char c[] =  {(char)(i+'a')};
			System.out.format("Size of %c: %d\n", c[0], uf.getSize( new String(c) ));
		}
		System.out.println();
		
		uf.doUnion("a", "b");
		uf.doUnion("c", "d");
		uf.doUnion("d", "a");
		
		for (int i = 0; i < 6; i++) {
			char c[] =  {(char)(i+'a')};
			System.out.format("Size of %c: %d\n", c[0], uf.getSize( new String(c) ));
		}
		System.out.println();
		
		uf.insert("c", new NullMapping());
		
		for (int i = 0; i < 6; i++) {
			char c[] =  {(char)(i+'a')};
			System.out.format("Size of %c: %d\n", c[0], uf.getSize( new String(c) ));
		}
	}
}
